package tt.authorization.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import tt.authorization.domain.request.LoginRequest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tt.authorization.helper.UserHelper.DEFAULT_PASSWORD;
import static tt.authorization.helper.UserHelper.JONNY_EMAIL;
import static tt.authorization.helper.UserHelper.loginRequest;

class LoginRequestValidationTest {

  private Validator validator;

  @BeforeEach
  void setup() {
    final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void shouldSuccessValidate() {
    final Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest());
    assertTrue(violations.isEmpty());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"  ", "\t\n\r "})
  void shouldReturnErrorWhenEmailIsBlank(final String email) {
    final LoginRequest request =
        LoginRequest.builder().email(email).password(DEFAULT_PASSWORD).build();
    final Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
    final String errorMessage = violations.iterator().next().getMessage();

    assertFalse(violations.isEmpty());
    assertThat(errorMessage, is("Email must not be blank"));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"  ", "\t\n\r "})
  void shouldReturnErrorWhenPasswordIsBlank(final String password) {
    final LoginRequest request =
        LoginRequest.builder().email(JONNY_EMAIL).password(password).build();
    final Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
    final String errorMessage = violations.iterator().next().getMessage();

    assertFalse(violations.isEmpty());
    assertThat(errorMessage, is("Password must not be blank"));
  }
}
