package tt.authorization.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tt.authorization.config.jwt.AuthTokenFilter;
import tt.authorization.config.jwt.JwtUtils;
import tt.authorization.service.auth.UserDetailsServiceImpl;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private static final String API = "/api/v1";
    private static final String ALL_ROLES = "hasRole('ADMIN') or hasRole('USER')";
    private static final String ADMIN_ROLE = "hasRole('ADMIN')";

    private final RsaProperties rsaKeys;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public WebSecurityConfig(final RsaProperties rsaKeys, final JwtUtils jwtUtils,
            final UserDetailsServiceImpl userDetailsService) {
        this.rsaKeys = rsaKeys;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeys.getPublicKey()).privateKey(rsaKeys.getPrivateKey()).build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.getPublicKey()).build();
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
                                .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        //@formatter:on
        return http.build();
    }
}
