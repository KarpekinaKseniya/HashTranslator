package tt.hashtranslator.client;

import static java.lang.String.format;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tt.hashtranslator.domain.request.LoginRequest;
import tt.hashtranslator.exception.AuthClientException;

@Component
public class AuthClientImpl implements AuthClient {

  private final RestTemplate client;
  private final String authUrl;

  public AuthClientImpl(final RestTemplate client,
      @Value("${auth.service.url}") final String authUrl) {
    this.client = client;
    this.authUrl = authUrl;
  }

  @Override
  public HttpHeaders login(final LoginRequest request) {
    final HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(request);
    final ResponseEntity<String> response = client.exchange(authUrl + "/login", POST, loginEntity,
        String.class);
    if (!OK.equals(response.getStatusCode())) {
      throw new AuthClientException(
          format("Auth client failed with status code = %s", response.getStatusCode()),
          response.getStatusCode().value());
    }
    return response.getHeaders();
  }
}
