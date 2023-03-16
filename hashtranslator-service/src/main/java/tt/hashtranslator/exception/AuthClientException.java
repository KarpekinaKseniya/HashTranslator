package tt.hashtranslator.exception;

public class AuthClientException extends RuntimeException {

  private final int statusCode;

  public AuthClientException(final String message, final int statusCode) {
    super(message);
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }
}
