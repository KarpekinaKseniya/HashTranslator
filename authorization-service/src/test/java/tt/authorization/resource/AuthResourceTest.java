package tt.authorization.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import tt.authorization.service.RefreshTokenService;
import tt.authorization.service.UserService;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.ResponseEntity.ok;
import static tt.authorization.helper.UserHelper.loginRequest;

class AuthResourceTest {

  private static final String LOGOUT_MESSAGE = "You've been sighed out!";
  private static final String REFRESH_TOKEN_MESSAGE = "Token is refreshed successfully!";

  @Mock private UserService userService;
  @Mock private RefreshTokenService refreshTokenService;
  @InjectMocks private AuthResource authResource;

  @BeforeEach
  void setup() {
    openMocks(this);
  }

  @Test
  void shouldAuth() {
    final String accessToken = "access_token";
    final String refreshToken = "refresh_token";

    given(userService.login(loginRequest())).willReturn(accessToken);
    given(refreshTokenService.createRefreshToken(loginRequest().getEmail()))
        .willReturn(refreshToken);

    final ResponseEntity<Void> actual = authResource.auth(loginRequest());
    assertThat(
        actual, is(ok().header(SET_COOKIE, accessToken).header(SET_COOKIE, refreshToken).build()));

    then(userService).should(only()).login(loginRequest());
    then(refreshTokenService).should(only()).createRefreshToken(loginRequest().getEmail());
  }

  @Test
  void shouldLogoutUser() {
    final User user = mock(User.class);
    final Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    given(securityContext.getAuthentication()).willReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    given(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).willReturn(user);

    given(userService.logout()).willReturn("null");
    given(refreshTokenService.cleanRefreshToken(any())).willReturn("null");

    final ResponseEntity<String> actual = authResource.logoutUser();
    assertThat(
        actual,
        is(ok().header(SET_COOKIE, "null").header(SET_COOKIE, "null").body(LOGOUT_MESSAGE)));

    then(userService).should(only()).logout();
    then(refreshTokenService).should(only()).cleanRefreshToken(any());
  }

  @Test
  void shouldRefreshToken() {
    final HttpServletRequest request = mock(HttpServletRequest.class);
    final String refreshToken = "token";

    given(refreshTokenService.findByToken(request)).willReturn(refreshToken);

    final ResponseEntity<String> actual = authResource.refreshToken(request);
    assertThat(actual, is(ok().header(SET_COOKIE, refreshToken).body(REFRESH_TOKEN_MESSAGE)));

    then(refreshTokenService).should(only()).findByToken(request);
  }
}
