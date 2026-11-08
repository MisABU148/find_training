package m.ilyina.find_trining.metrics;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class FaultInjectionFilter implements Filter {

    static final AtomicInteger errorPercent = new AtomicInteger(0);
    static final AtomicLong delayMs = new AtomicLong(0);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String uri = ((HttpServletRequest) req).getRequestURI();
        if (uri.startsWith("/api/") && !uri.startsWith("/api/debug")) {
            long d = delayMs.get();
            if (d > 0) {
                try { Thread.sleep(d); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            }
            if (ThreadLocalRandom.current().nextInt(100) < errorPercent.get()) {
                ((HttpServletResponse) res).sendError(500, "injected failure");
                return;
            }
        }
        chain.doFilter(req, res);
    }

    @RestController
    @RequestMapping("/api/debug")
    static class Controller {
        @PostMapping("/errors")
        public String errors(@RequestParam int percent) {
            errorPercent.set(percent);
            return "errorPercent=" + percent;
        }

        @PostMapping("/delay")
        public String delay(@RequestParam long ms) {
            delayMs.set(ms);
            return "delayMs=" + ms;
        }
    }
}