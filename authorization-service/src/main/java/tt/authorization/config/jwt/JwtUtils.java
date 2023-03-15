package tt.authorization.config.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;
import tt.authorization.exception.TokenException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;

@Component
public class JwtUtils {

  private static final String API = "/api/v1";
  private static final String REFRESH_API = API + "/auth/token/refresh";
  private final String jwtCookie;
  private final String jwtRefreshCookie;
  private final int jwtCookieAgeSec;

  public JwtUtils(
      @Value("${app.jwtCookieName}") final String jwtCookie,
      @Value("${app.jwtRefreshCookieName}") final String jwtRefreshCookie,
      @Value("${app.jwtCookieAgeSec}") final int jwtCookieAgeSec) {
    this.jwtCookie = jwtCookie;
    this.jwtRefreshCookie = jwtRefreshCookie;
    this.jwtCookieAgeSec = jwtCookieAgeSec;
  }

  public ResponseCookie generateAccessJwtCookie(final String accessToken) {
    return generateCookie(jwtCookie, accessToken, API);
  }

  public ResponseCookie generateRefreshJwtCookie(final String refreshToken) {
    return generateCookie(jwtRefreshCookie, refreshToken, REFRESH_API);
  }

  public String getJwtFromCookies(final HttpServletRequest request) {
    return getCookieValueByName(request, jwtCookie);
  }

  public ResponseCookie cleanAccessToken() {
    return ResponseCookie.from(jwtCookie, null).maxAge(0).path(API).build();
  }

  public ResponseCookie cleanRefreshToken() {
    return ResponseCookie.from(jwtRefreshCookie, null).maxAge(0).path(REFRESH_API).build();
  }

  public String getUserNameFromJwtToken(final String token) {
    return parseToken(token).getSubject();
  }

  public boolean validateJwtToken(final String token) {
    final JWTClaimsSet jwt = parseToken(token);
    Assert.notNull(jwt, "JWT argument cannot be null.");
    if (jwt.getExpirationTime().compareTo(new Date()) < 0) {
      throw new TokenException(
          jwt.getSubject(), "Token was expired. Please make a new login request");
    }
    return true;
  }

  private JWTClaimsSet parseToken(final String token) {
    try {
      final SignedJWT decodedJWT = SignedJWT.parse(token);
      return decodedJWT.getJWTClaimsSet();
    } catch (final ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  private ResponseCookie generateCookie(final String name, final String value, final String path) {
    return ResponseCookie.from(name, value)
        .path(path)
        .maxAge(this.jwtCookieAgeSec)
        .httpOnly(true)
        .build();
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
