package m.ilyina.find_trining.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
}
