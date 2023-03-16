package tt.authorization.exception;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(FORBIDDEN)
public class TokenException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public TokenException(final String token, final String message) {
    super(format("Failed for [%s]: %s", token, message));
  }
}
