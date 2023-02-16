package tt.authorization.config;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import tt.authorization.config.jwt.AuthEntryPointJwt;
import tt.authorization.service.auth.UserDetailsServiceImpl;

//TODO
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;

    public WebSecurityConfig(final UserDetailsServiceImpl userDetailsService,
            final AuthEntryPointJwt unauthorizedHandler) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        throw new NotImplementedException();
    }

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration authConfig) throws Exception {
        throw new NotImplementedException();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        throw new NotImplementedException();
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        throw new NotImplementedException();
    }
}
