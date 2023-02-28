package tt.authorization.exception.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import tt.authorization.domain.response.ErrorResponse;
import tt.authorization.exception.EntityNotFoundException;
import tt.authorization.exception.TokenException;

@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler
  @ResponseStatus(value = BAD_REQUEST)
  public ErrorResponse handleIllegalArgumentException(
      final IllegalArgumentException ex, final WebRequest request) {
    return new ErrorResponse(BAD_REQUEST.value(), ex.getMessage(), request.getDescription(false));
  }

  @ExceptionHandler
  @ResponseStatus(value = BAD_REQUEST)
  public ErrorResponse handleValidationError(
      final MethodArgumentNotValidException ex, final WebRequest request) {
    return new ErrorResponse(
        BAD_REQUEST.value(),
        ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .sorted()
            .collect(Collectors.joining(", ")),
        request.getDescription(false));
  }

  @ExceptionHandler
  @ResponseStatus(value = BAD_REQUEST)
  public ErrorResponse handleConstraintViolationException(
      final ConstraintViolationException ex, final WebRequest request) {
    return new ErrorResponse(BAD_REQUEST.value(), ex.getMessage(), request.getDescription(false));
  }

  @ExceptionHandler
  @ResponseStatus(value = UNAUTHORIZED)
  public ErrorResponse handleUsernameNotFoundException(
      final UsernameNotFoundException ex, final WebRequest request) {
    return new ErrorResponse(UNAUTHORIZED.value(), ex.getMessage(), request.getDescription(false));
  }

  @ExceptionHandler
  @ResponseStatus(value = UNAUTHORIZED)
  public ErrorResponse handleAuthenticationException(
      final AuthenticationException ex, final WebRequest request) {
    return new ErrorResponse(UNAUTHORIZED.value(), ex.getMessage(), request.getDescription(false));
  }

  @ExceptionHandler({EntityNotFoundException.class})
  @ResponseStatus(value = NOT_FOUND)
  public ErrorResponse handleEntityNotFoundException(
      final EntityNotFoundException ex, final WebRequest request) {
    return new ErrorResponse(NOT_FOUND.value(), ex.getMessage(), request.getDescription(false));
  }

  @ExceptionHandler({DataIntegrityViolationException.class})
  @ResponseStatus(value = CONFLICT)
  public ErrorResponse handleDataIntegrityViolation(
      final DataIntegrityViolationException ex, final WebRequest request) {
    return new ErrorResponse(
        CONFLICT.value(), ex.getCause().getMessage(), request.getDescription(false));
  }

  @ExceptionHandler(value = TokenException.class)
  @ResponseStatus(FORBIDDEN)
  public ErrorResponse handleTokenRefreshException(
          final TokenException ex, final WebRequest request) {
    return new ErrorResponse(FORBIDDEN.value(), ex.getMessage(), request.getDescription(false));
  }

  @ExceptionHandler
  @ResponseStatus(value = INTERNAL_SERVER_ERROR)
  public ErrorResponse handleGlobalException(final Exception ex, final WebRequest request) {
    return new ErrorResponse(
        INTERNAL_SERVER_ERROR.value(), ex.getMessage(), request.getDescription(false));
  }
}
