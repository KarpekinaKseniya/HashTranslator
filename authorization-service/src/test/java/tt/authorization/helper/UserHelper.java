package tt.authorization.helper;

import tt.authorization.domain.entity.Roles;
import tt.authorization.domain.entity.User;
import tt.authorization.domain.request.CreateUserRequest;
import tt.authorization.domain.request.LoginRequest;
import tt.authorization.domain.response.UserInfoResponse;

import java.util.Set;

import static tt.authorization.domain.entity.ERole.ROLE_USER;

public class UserHelper {

  public static String DEFAULT_PASSWORD = "password";
  public static String ENCODE_PASSWORD =
      "$2a$10$r.ctB43XcviDPD0trfxdUeo7vag/N2JqnHGtXm7/sBmXjn567MEEm";

  private static String JONNY_EMAIL = "test@gmail.ru";
  private static String JONNY_FIRSTNAME = "John";
  private static String JONNY_LASTNAME = "Test";

  public static User.UserBuilder userEntityBuilder() {
    return User.builder()
        .email(JONNY_EMAIL)
        .firstname(JONNY_FIRSTNAME)
        .lastname(JONNY_LASTNAME)
        .password(ENCODE_PASSWORD)
        .roles(Set.of(defaultRole()));
  }

  public static CreateUserRequest createUserRequest() {
    return CreateUserRequest.builder()
        .email(JONNY_EMAIL)
        .firstname(JONNY_FIRSTNAME)
        .lastname(JONNY_LASTNAME)
        .password(DEFAULT_PASSWORD)
        .role(Set.of(ROLE_USER))
        .build();
  }

  public static UserInfoResponse userInfoResponse() {
    return UserInfoResponse.builder()
        .email(JONNY_EMAIL)
        .firstname(JONNY_FIRSTNAME)
        .lastname(JONNY_LASTNAME)
        .roles(Set.of(defaultRole()))
        .build();
  }

  public static LoginRequest loginRequest() {
    return LoginRequest.builder().email(JONNY_EMAIL).password(DEFAULT_PASSWORD).build();
  }

  public static Roles defaultRole() {
    return Roles.builder().id(2L).name(ROLE_USER).build();
  }
}
