package tt.authorization.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import tt.authorization.config.jwt.JwtUtils;
import tt.authorization.domain.request.LoginRequest;

@Service
public class TokenServiceImpl implements TokenService {

    private final JwtEncoder jwtEncoder;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final String jwtRefreshCookie;
    private final int jwtExpirationSec;
    private final int refreshJwtExpirationSec;

    public TokenServiceImpl(final JwtEncoder jwtEncoder, final UserDetailsService userDetailsService,
            final AuthenticationManager authenticationManager, final JwtUtils jwtUtils,
            @Value("${app.jwtExpirationSec}") final int jwtExpirationSec,
                            @Value("${app.jwtRefreshCookieName}") final String jwtRefreshCookie,
            @Value("${app.jwtRefreshExpirationSec}") final int refreshJwtExpirationSec) {
        this.jwtEncoder = jwtEncoder;
        this.userDetailsService = userDetailsService;
        this.authManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.jwtExpirationSec = jwtExpirationSec;
        this.refreshJwtExpirationSec = refreshJwtExpirationSec;
        this.jwtRefreshCookie = jwtRefreshCookie;
    }

    @Override
    public String generateAccessToken(final LoginRequest request) {
        final UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        final Authentication auth = authManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(auth);

        return generateAccessToken(request.getEmail());
    }

    @Override
    public String generateRefreshToken(final String email) {
        final UserDetails userDetails = getUserDetails(email);
        final String token = generateRefreshToken(userDetails);
        return jwtUtils.generateRefreshJwtCookie(token).toString();
    }

    @Override
    public String findByToken(final HttpServletRequest request) {
        final String refreshToken =
                Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(this.jwtRefreshCookie))
                        .map(Cookie::getValue).findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Refresh Token is not found!"));

        jwtUtils.validateJwtToken(refreshToken);

        final String email = jwtUtils.getUserNameFromJwtToken(refreshToken);
        return generateAccessToken(email);
    }

    @Override
    public String cleanAccessToken() {
        return jwtUtils.cleanAccessToken().toString();
    }

    @Override
    public String cleanRefreshToken() {
        return jwtUtils.cleanRefreshToken().toString();
    }

    private String generateAccessToken(final String email) {
        final UserDetails userDetails = getUserDetails(email);
        final Instant now = Instant.now();
        final String scope = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        //@formatter:off
        final JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .claim("scope", scope)
                .expiresAt(now.plus(this.jwtExpirationSec, ChronoUnit.SECONDS))
                .subject(userDetails.getUsername()).build();
        //@formatter:on

        final String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return jwtUtils.generateAccessJwtCookie(token).toString();
    }

    private String generateRefreshToken(final UserDetails userDetails) {
        final Instant now = Instant.now();
        final String scope = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        //@formatter:off
        final JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now.plus(this.refreshJwtExpirationSec, ChronoUnit.SECONDS))
                .claim("scope", scope)
                .subject(userDetails.getUsername()).build();
        //@formatter:on
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private UserDetails getUserDetails(final String email) {
        return userDetailsService.loadUserByUsername(email);
    }
}
