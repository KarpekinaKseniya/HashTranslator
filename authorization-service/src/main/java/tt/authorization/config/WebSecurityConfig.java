package tt.authorization.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tt.authorization.config.jwt.AuthEntryPointJwt;
import tt.authorization.config.jwt.AuthTokenFilter;
import tt.authorization.config.jwt.JwtUtils;
import tt.authorization.service.auth.UserDetailsServiceImpl;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private static final String API = "/api/v1";
    private static final String ALL_ROLES = "hasRole('ADMIN') or hasRole('USER')";
    private static final String ADMIN_ROLE = "hasRole('ADMIN')";

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final JwtUtils jwtUtils;

    public WebSecurityConfig(final UserDetailsServiceImpl userDetailsService,
            final AuthEntryPointJwt unauthorizedHandler, final JwtUtils jwtUtils) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration authConfiguration)
            throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        //@formatter:off
        http.cors(CorsConfigurer::disable)
                .csrf(CsrfConfigurer::disable).
                        authorizeRequests(
                        auth -> auth.antMatchers(API + "/auth/**").permitAll()
                                .antMatchers(GET, API + "/account/{\\d+}").access(ALL_ROLES)
                                .antMatchers(DELETE, API + "/account/{\\d+}").access(ADMIN_ROLE)
                                .antMatchers(POST, API + "/account").access(ADMIN_ROLE)
                                .antMatchers("/swagger-ui-custom.html", "/swagger-ui.html", "/swagger-ui/**",
                                        "/v3/api-docs/**", "/webjars/**", "/swagger-ui/index.html", "/api-docs/**").permitAll()
                                .anyRequest().permitAll())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        //@formatter:on
        return http.build();
    }
}
