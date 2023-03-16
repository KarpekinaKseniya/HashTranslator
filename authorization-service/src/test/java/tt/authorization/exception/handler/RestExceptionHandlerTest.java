package tt.authorization.exception.handler;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_CONFLICT;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.MockitoAnnotations.openMocks;

import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import tt.authorization.domain.response.ErrorResponse;
import tt.authorization.exception.EntityNotFoundException;
import tt.authorization.exception.TokenException;

class RestExceptionHandlerTest {

  private static final String DESCRIPTION = "Some description";
  private static final String ERROR_MESSAGE = "Error message";

  private final ErrorResponse.ErrorResponseBuilder errorResponse =
      ErrorResponse.builder().description(DESCRIPTION).message(ERROR_MESSAGE);

  @Mock private WebRequest webRequest;
  @InjectMocks private RestExceptionHandler handler;

  @BeforeEach
  void setup() {
    openMocks(this);
    given(webRequest.getDescription(false)).willReturn(DESCRIPTION);
  }

  @AfterEach
  void verify() {
    then(webRequest).should(only()).getDescription(false);
  }

  @Test
  void shouldHandleIllegalArgumentException() {
    final IllegalArgumentException exception = new IllegalArgumentException(ERROR_MESSAGE);

    final ErrorResponse actual = handler.handleIllegalArgumentException(exception, webRequest);
    assertThat(actual, is(errorResponse.statusCode(SC_BAD_REQUEST).build()));
  }

  @Test
  void shouldHandleValidationError() {
    final String defaultMessage = "Cat name can't blank";
    final BindingResult bindingResult = mock(BindingResult.class);
    final MethodArgumentNotValidException exception =
        new MethodArgumentNotValidException(null, bindingResult);
    final FieldError fieldError = mock(FieldError.class);

    given(bindingResult.getFieldErrors()).willReturn(singletonList(fieldError));
    given(fieldError.getDefaultMessage()).willReturn(defaultMessage);

    final ErrorResponse actual = handler.handleValidationError(exception, webRequest);
    assertThat(
        actual, is(errorResponse.statusCode(SC_BAD_REQUEST).message(defaultMessage).build()));

    then(bindingResult).should(only()).getFieldErrors();
    then(fieldError).should(only()).getDefaultMessage();
  }

  @Test
  void shouldHandleConstraintViolationException() {
    final ConstraintViolationException exception = mock(ConstraintViolationException.class);

    given(exception.getMessage()).willReturn(ERROR_MESSAGE);

    final ErrorResponse actual = handler.handleConstraintViolationException(exception, webRequest);
    assertThat(actual, is(errorResponse.statusCode(SC_BAD_REQUEST).build()));
  }

  @Test
  void shouldHandleUsernameNotFoundException() {
    final UsernameNotFoundException exception = new UsernameNotFoundException(ERROR_MESSAGE);

    final ErrorResponse actual = handler.handleUsernameNotFoundException(exception, webRequest);
    assertThat(actual, is(errorResponse.statusCode(SC_UNAUTHORIZED).build()));
  }

  @Test
  void shouldHandleAuthenticationException() {
    final AuthenticationException exception = new BadCredentialsException(ERROR_MESSAGE);

    final ErrorResponse actual = handler.handleAuthenticationException(exception, webRequest);
    assertThat(actual, is(errorResponse.statusCode(SC_UNAUTHORIZED).build()));
  }

  @Test
  void shouldHandleEntityNotFoundException() {
    final EntityNotFoundException exception = new EntityNotFoundException(ERROR_MESSAGE);

    final ErrorResponse actual = handler.handleEntityNotFoundException(exception, webRequest);
    assertThat(actual, is(errorResponse.statusCode(SC_NOT_FOUND).build()));
  }

  @Test
  void shouldHandleDataIntegrityViolation() {
    final DataIntegrityViolationException exception =
        new DataIntegrityViolationException(
            "other error message", new RuntimeException(ERROR_MESSAGE));

    final ErrorResponse actual = handler.handleDataIntegrityViolation(exception, webRequest);
    assertThat(actual, is(errorResponse.statusCode(SC_CONFLICT).build()));
  }

  @Test
  void shouldHandleGlobalException() {
    final Exception exception = new RuntimeException(ERROR_MESSAGE);

    final ErrorResponse actual = handler.handleGlobalException(exception, webRequest);
    assertThat(actual, is(errorResponse.statusCode(SC_INTERNAL_SERVER_ERROR).build()));
  }

  @Test
  void handleTokenRefreshException() {
    final String token = "some_token";
    final TokenException exception = new TokenException(token, ERROR_MESSAGE);

    final ErrorResponse actual = handler.handleTokenRefreshException(exception, webRequest);
    assertThat(
        actual,
        is(
            errorResponse
                .message(format("Failed for [%s]: %s", token, ERROR_MESSAGE))
                .statusCode(SC_FORBIDDEN)
                .build()));
  }
}
