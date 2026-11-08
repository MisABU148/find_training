package m.ilyina.find_trining.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import java.time.Duration;

/**
 * Only the {@link PasswordEncoder} bean is wired up for now, so that
 * {@code User} passwords are never stored in plain text - full JWT-based
 * authentication/authorization will be added later on top of this.
 */
@Configuration
public class SecurityBeansConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // для метрик sli
    @Bean
    public MeterFilter httpRequestsHistogram() {
        return new MeterFilter() {
            @Override
            public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                if ("http.server.requests".equals(id.getName())) {
                    return DistributionStatisticConfig.builder()
                            .percentilesHistogram(true)
                            .serviceLevelObjectives(
                                    (double) Duration.ofMillis(100).toNanos(),
                                    (double) Duration.ofMillis(500).toNanos(),
                                    (double) Duration.ofSeconds(1).toNanos(),
                                    (double) Duration.ofSeconds(2).toNanos())
                            .build()
                            .merge(config);
                }
                return config;
            }
        };
    }
}
