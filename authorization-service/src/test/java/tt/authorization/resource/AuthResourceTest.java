package tt.authorization.resource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import tt.authorization.service.TokenService;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.ResponseEntity.ok;
import static tt.authorization.helper.UserHelper.loginRequest;

class AuthResourceTest {

  private static final String LOGOUT_MESSAGE = "You've been sighed out!";
  private static final String REFRESH_TOKEN_MESSAGE = "Token is refreshed successfully!";

  @Mock private TokenService tokenService;
  @InjectMocks private AuthResource authResource;

  @BeforeEach
  void setup() {
    openMocks(this);
  }

  @AfterEach
  void verify() {
    verifyNoMoreInteractions(tokenService);
  }

  @Test
  void shouldAuth() {
    final String accessToken = "access_token";
    final String refreshToken = "refresh_token";

    given(tokenService.generateAccessToken(loginRequest())).willReturn(accessToken);
    given(tokenService.generateRefreshToken(loginRequest().getEmail())).willReturn(refreshToken);

    final ResponseEntity<Void> actual = authResource.auth(loginRequest());
    assertThat(
        actual, is(ok().header(SET_COOKIE, accessToken).header(SET_COOKIE, refreshToken).build()));

    then(tokenService).should(times(1)).generateAccessToken(loginRequest());
    then(tokenService).should(times(1)).generateRefreshToken(loginRequest().getEmail());
  }

  @Test
  void shouldLogoutUser() {
    given(tokenService.cleanAccessToken()).willReturn("null");
    given(tokenService.cleanRefreshToken()).willReturn("null");

    final ResponseEntity<String> actual = authResource.logoutUser();
    assertThat(
        actual,
        is(ok().header(SET_COOKIE, "null").header(SET_COOKIE, "null").body(LOGOUT_MESSAGE)));

    then(tokenService).should(times(1)).cleanAccessToken();
    then(tokenService).should(times(1)).cleanRefreshToken();
  }

  @Test
  void shouldRefreshToken() {
    final HttpServletRequest request = mock(HttpServletRequest.class);
    final String refreshToken = "token";

    given(tokenService.findByToken(request)).willReturn(refreshToken);

    final ResponseEntity<String> actual = authResource.refreshToken(request);
    assertThat(actual, is(ok().header(SET_COOKIE, refreshToken).body(REFRESH_TOKEN_MESSAGE)));

    then(tokenService).should(only()).findByToken(request);
  }
}
