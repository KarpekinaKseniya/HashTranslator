package tt.hashtranslator.interceptor;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

import java.util.Arrays;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import tt.hashtranslator.exception.AuthClientException;

@Component
public class AuthInterceptor implements HandlerInterceptor {

  private final RestTemplate client;
  private final String authUrl;
  private final String cookieName;

  public AuthInterceptor(final RestTemplate client,
      @Value("${auth.service.url}") final String authUrl,
      @Value("${jwt.cookie.name}") final String cookieName) {
    this.client = client;
    this.authUrl = authUrl;
    this.cookieName = cookieName;
  }

  @Override
  public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
      final Object handler) {
    final Cookie cookie = findCookie(request);
    final String responseCookie = transformToResponseCookie(cookie).toString();
    final HttpEntity<String> httpEntity = new HttpEntity<>(responseCookie);
    final ResponseEntity<Void> authResponse = client.exchange(authUrl, POST, httpEntity,
        Void.class);
    if (authResponse.getStatusCode().value() != OK.value()) {
      throw new AuthClientException("Unauthorized. Check your credentials",
          authResponse.getStatusCode().value());
    }
    return true;
  }

  private Cookie findCookie(final HttpServletRequest request) {
    return Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(cookieName))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Token is not found!"));
  }

  private ResponseCookie transformToResponseCookie(final Cookie cookie) {
    return ResponseCookie.from(cookie.getName(), cookie.getValue())
        .path(cookie.getPath())
        .maxAge(cookie.getMaxAge()).httpOnly(true).build();
  }
}
