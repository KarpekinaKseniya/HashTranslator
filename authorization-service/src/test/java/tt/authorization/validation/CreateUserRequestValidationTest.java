package tt.authorization.validation;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import tt.authorization.domain.request.CreateUserRequest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tt.authorization.domain.entity.ERole.ROLE_USER;
import static tt.authorization.helper.UserHelper.JONNY_EMAIL;

class CreateUserRequestValidationTest {

  private static final String BIG_STRING = StringUtils.repeat("*", 100);

  private Validator validator;

  @BeforeEach
  void setup() {
    final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void shouldSuccessValidate() {
    final Set<ConstraintViolation<CreateUserRequest>> violations =
        validator.validate(getCreateUserRequest().build());
    assertTrue(violations.isEmpty());
  }

  @Test
  void shouldReturnErrorWhenInvalidSizeOfFirstName() {
    final CreateUserRequest request = getCreateUserRequest().firstname(BIG_STRING).build();
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
    final String errorMessage = violations.iterator().next().getMessage();

    assertFalse(violations.isEmpty());
    assertThat(errorMessage, is("Firstname size must be between 0 and 25"));
  }

  @Test
  void shouldReturnErrorWhenInvalidSizeOfLastName() {
    final CreateUserRequest request = getCreateUserRequest().lastname(BIG_STRING).build();
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
    final String errorMessage = violations.iterator().next().getMessage();

    assertFalse(violations.isEmpty());
    assertThat(errorMessage, is("Lastname size must be between 0 and 25"));
  }

  @Test
  void shouldReturnErrorWhenInvalidSizeOfPassword() {
    final CreateUserRequest request = getCreateUserRequest().password(BIG_STRING).build();
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
    final String errorMessage = violations.iterator().next().getMessage();

    assertFalse(violations.isEmpty());
    assertThat(errorMessage, is("Password size must be between 0 and 60"));
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnErrorWhenEmailIsBlank(final String email) {
    final CreateUserRequest request = getCreateUserRequest().email(email).build();
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
    final String errorMessage = violations.iterator().next().getMessage();

    assertFalse(violations.isEmpty());
    assertThat(errorMessage, is("Email must not be blank"));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"test.@gmail.com", ".user.name@gmail.com", "test-name@gmail.com.", "ala@.com"})
  void shouldReturnErrorWhenWrongEmail(final String email) {
    final CreateUserRequest request = getCreateUserRequest().email(email).build();
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
    final String errorMessage = violations.iterator().next().getMessage();

    assertFalse(violations.isEmpty());
    assertThat(errorMessage, is("must be a well-formed email address"));
  }

  @Test
  void shouldReturnErrorWhenPasswordIsNull() {
    final CreateUserRequest request = getCreateUserRequest().password(null).build();
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
    final String errorMessage = violations.iterator().next().getMessage();

    assertFalse(violations.isEmpty());
    assertThat(errorMessage, is("Password must not be null"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"abcdg1", "abcdG1", "Abc!1", "abcdert", " ", "\t\n\r"})
  void shouldReturnErrorWhenWrongPassword(final String password) {
    final CreateUserRequest request = getCreateUserRequest().password(password).build();
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
    final String errorMessage = violations.iterator().next().getMessage();

    assertFalse(violations.isEmpty());
    assertThat(errorMessage, containsString("Password must contain"));
  }

  private CreateUserRequest.CreateUserRequestBuilder getCreateUserRequest() {
    return CreateUserRequest.builder()
        .email(JONNY_EMAIL)
        .firstname("Jonny")
        .lastname("Test")
        .password("Tyk1:21@1!des")
        .role(Set.of(ROLE_USER));
  }
}
