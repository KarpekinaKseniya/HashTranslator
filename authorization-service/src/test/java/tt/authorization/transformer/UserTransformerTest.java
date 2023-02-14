package tt.authorization.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.verification.VerificationMode;
import org.springframework.security.crypto.password.PasswordEncoder;
import tt.authorization.domain.entity.Roles;
import tt.authorization.domain.entity.User;
import tt.authorization.domain.request.CreateUserRequest;
import tt.authorization.domain.response.UserInfoResponse;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.MockitoAnnotations.openMocks;
import static tt.authorization.helper.UserHelper.DEFAULT_PASSWORD;
import static tt.authorization.helper.UserHelper.ENCODE_PASSWORD;
import static tt.authorization.helper.UserHelper.createUserRequest;
import static tt.authorization.helper.UserHelper.defaultRole;
import static tt.authorization.helper.UserHelper.userEntityBuilder;
import static tt.authorization.helper.UserHelper.userInfoResponse;

class UserTransformerTest {

  @Mock private PasswordEncoder passwordEncoder;

  private UserTransformer transformer;

  @BeforeEach
  void setup() {
    openMocks(this);
    transformer = new UserTransformerImpl(passwordEncoder);
  }

  private static Stream<Arguments> provideUserRequestAndEntity() {
    return Stream.of(
        Arguments.of(null, null, never(), null),
        Arguments.of(
            null,
            Set.of(defaultRole()),
            never(),
            User.builder().roles(Set.of(defaultRole())).build()),
        Arguments.of(
            createUserRequest(), Set.of(defaultRole()), only(), userEntityBuilder().build()),
        Arguments.of(
            createUserRequest(), null, only(), userEntityBuilder().roles(emptySet()).build()));
  }

  private static Stream<Arguments> provideUserEntityAndResponse() {
    return Stream.of(
        Arguments.of(null, null), Arguments.of(userEntityBuilder().build(), userInfoResponse()));
  }

  @ParameterizedTest
  @MethodSource("provideUserRequestAndEntity")
  void userRequestToEntity(
      final CreateUserRequest request,
      final Set<Roles> roles,
      final VerificationMode verify,
      final User expected) {
    given(passwordEncoder.encode(DEFAULT_PASSWORD)).willReturn(ENCODE_PASSWORD);

    assertThat(transformer.userRequestToEntity(request, roles), is(expected));

    then(passwordEncoder).should(verify).encode(DEFAULT_PASSWORD);
  }

  @ParameterizedTest
  @MethodSource("provideUserEntityAndResponse")
  void entityToResponse(final User entity, final UserInfoResponse response) {
    assertThat(transformer.entityToResponse(entity), is(response));
  }
}
