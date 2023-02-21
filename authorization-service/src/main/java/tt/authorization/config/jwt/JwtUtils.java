package tt.authorization.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;
import tt.authorization.domain.entity.User;
import tt.authorization.service.auth.UserDetailsImpl;

import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtUtils {

  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
  private static final String API = "/api/v1";
  private static final String REFRESH_API = API + "/auth/refreshtoken";

  private final String jwtSecret;
  private final int jwtExpirationMs;
  private final String jwtCookie;
  private final String jwtRefreshCookie;

  public JwtUtils(
      @Value("${app.jwtSecret}") final String jwtSecret,
      @Value("${app.jwtExpirationMs}") final int jwtExpirationMs,
      @Value("${app.jwtCookieName}") final String jwtCookie,
      @Value("${app.jwtRefreshCookieName}") final String jwtRefreshCookie) {
    this.jwtSecret = jwtSecret;
    this.jwtExpirationMs = jwtExpirationMs;
    this.jwtCookie = jwtCookie;
    this.jwtRefreshCookie = jwtRefreshCookie;
  }

  public String getJwtFromCookies(final HttpServletRequest request) {
    return getCookieValueByName(request, jwtCookie);
  }

  public ResponseCookie generateJwtCookie(final UserDetailsImpl userPrincipal) {
    final String jwt = generateTokenFromEmail(userPrincipal.getUsername());
    return generateCookie(jwtCookie, jwt, API);
  }

  public ResponseCookie generateJwtCookie(final User user) {
    String jwt = generateTokenFromEmail(user.getEmail());
    return generateCookie(jwtCookie, jwt, API);
  }

  public ResponseCookie generateRefreshJwtCookie(final String refreshToken) {
    return generateCookie(jwtRefreshCookie, refreshToken, REFRESH_API);
  }

  public String getJwtRefreshFromCookies(final HttpServletRequest request) {
    return getCookieValueByName(request, jwtRefreshCookie);
  }

  public ResponseCookie getCleanJwtRefreshCookie() {
    ResponseCookie cookie = ResponseCookie.from(jwtRefreshCookie, null).path(REFRESH_API).build();
    return cookie;
  }

  public ResponseCookie getCleanJwtCookie() {
    return ResponseCookie.from(jwtCookie, null).path(API).build();
  }

  public String getUserNameFromJwtToken(final String token) {
    return Jwts.parserBuilder()
        .setSigningKey(convertStringToSecretKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public boolean validateJwtToken(final String authToken) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(convertStringToSecretKey())
          .build()
          .parseClaimsJws(authToken);
      return true;
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }

  public String generateTokenFromEmail(final String email) {
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(convertStringToSecretKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  private SecretKey convertStringToSecretKey() {
    final byte[] decodedKey = Decoders.BASE64.decode(this.jwtSecret);
    return Keys.hmacShaKeyFor(decodedKey);
  }

  private ResponseCookie generateCookie(final String name, final String value, final String path) {
    return ResponseCookie.from(name, value).path(path).maxAge(24 * 60 * 60).httpOnly(true).build();
  }

  private String getCookieValueByName(final HttpServletRequest request, final String name) {
    Cookie cookie = WebUtils.getCookie(request, name);
    if (cookie != null) {
      return cookie.getValue();
    } else {
      return null;
    }
  }
}
