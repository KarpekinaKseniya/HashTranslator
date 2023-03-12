package tt.authorization.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@ResponseStatus(FORBIDDEN)
public class TokenException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public TokenException(String token, String message) {
    super(format("Failed for [%s]: %s", token, message));
  }
}
