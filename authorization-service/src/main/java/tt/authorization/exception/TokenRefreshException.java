package tt.authorization.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@ResponseStatus(FORBIDDEN)
public class TokenRefreshException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public TokenRefreshException(String token, String message) {
    super(String.format("Failed for [%s]: %s", token, message));
  }
}
