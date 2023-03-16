package tt.authorization.service;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;
import static tt.authorization.helper.UserHelper.DEFAULT_PASSWORD;
import static tt.authorization.helper.UserHelper.JONNY_EMAIL;
import static tt.authorization.helper.UserHelper.loginRequest;
import static tt.authorization.helper.UserHelper.userEntityBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import tt.authorization.config.jwt.JwtUtils;
import tt.authorization.service.auth.UserDetailsImpl;

class TokenServiceTest {

  private static final int JWT_EXPIRATION_SEC = 600;
  private static final int REFRESH_JWT_EXPIRATION_SEC = 1200;
  private static final String JWT_REFRESH_COOKIE = "test_refresh_cookie";
  private static final Exception EXCEPTION = new RuntimeException("some error message");
  private static final String NOT_FOUND_REFRESH_TOKEN = "Refresh Token is not found!";
  private static final String TOKEN =
      "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.NHVaYe26MbtOYhSKkoKYdFVomg4i8ZJd8_-RU8VNbftc4TSMb4bXP3l3YlNWACwyXPGffz5aXHc6lty1Y2t4SWRqGteragsVdZufDn5BlnJl9pdR_kdVFUsra2rWKEofkZeIC4yWytE58sMIihvo9H1ScmmVwBcQP6XETqYd0aSHp1gOa9RdUPDvoXQ5oqygTqVtxaDr6wUFKrKItgBMzWIdNZ6y7O9E0DhEPTbE9rfBo6KTFsHAZnMg4k68CDp2woYIaXbmYTWcvbzIuHO7_37GT79XdIwkm95QJ7hYC9RiwrV7mesbY4PAahERJawntho0my942XheVLmGwLMBkQ";
  private static final UserDetails USER_DETAILS =
      UserDetailsImpl.build(userEntityBuilder().build());
  private static final ResponseCookie RESPONSE_COOKIE =
      ResponseCookie.from("name", TOKEN)
          .path("path")
          .maxAge(JWT_EXPIRATION_SEC)
          .httpOnly(true)
          .build();
  private final Jwt jwt = mock(Jwt.class);

  @Mock private JwtEncoder jwtEncoder;
  @Mock private UserDetailsService userDetailsService;
  @Mock private AuthenticationManager authManager;
  @Mock private JwtUtils jwtUtils;
  private TokenService service;

  @BeforeEach
  void setUp() {
    openMocks(this);
    service =
        new TokenServiceImpl(
            jwtEncoder,
            userDetailsService,
            authManager,
            jwtUtils,
            JWT_EXPIRATION_SEC,
            JWT_REFRESH_COOKIE,
            REFRESH_JWT_EXPIRATION_SEC);
  }

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(jwtEncoder, userDetailsService, authManager, jwtUtils);
  }

  @Test
  void shouldGenerateAccessToken() {
    final UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(JONNY_EMAIL, DEFAULT_PASSWORD);
    final Authentication auth = mock(Authentication.class);

    given(authManager.authenticate(authenticationToken)).willReturn(auth);
    given(userDetailsService.loadUserByUsername(JONNY_EMAIL)).willReturn(USER_DETAILS);
    given(jwtEncoder.encode(any())).willReturn(jwt);
    given(jwt.getTokenValue()).willReturn(TOKEN);
    given(jwtUtils.generateAccessJwtCookie(TOKEN)).willReturn(RESPONSE_COOKIE);

    final String actual = service.generateAccessToken(loginRequest());
    assertThat(actual, is(RESPONSE_COOKIE.toString()));

    then(authManager).should(only()).authenticate(authenticationToken);
    then(userDetailsService).should(only()).loadUserByUsername(JONNY_EMAIL);
    then(jwtEncoder).should(only()).encode(any());
    then(jwt).should(only()).getTokenValue();
    then(jwtUtils).should(only()).generateAccessJwtCookie(TOKEN);
  }

  @Test
  void shouldGenerateRefreshToken() {
    given(userDetailsService.loadUserByUsername(JONNY_EMAIL)).willReturn(USER_DETAILS);
    given(jwtEncoder.encode(any())).willReturn(jwt);
    given(jwt.getTokenValue()).willReturn(TOKEN);
    given(jwtUtils.generateRefreshJwtCookie(TOKEN)).willReturn(RESPONSE_COOKIE);

    final String actual = service.generateRefreshToken(JONNY_EMAIL);
    assertThat(actual, is(RESPONSE_COOKIE.toString()));

    then(userDetailsService).should(only()).loadUserByUsername(JONNY_EMAIL);
    then(jwtEncoder).should(only()).encode(any());
    then(jwt).should(only()).getTokenValue();
    then(jwtUtils).should(only()).generateRefreshJwtCookie(TOKEN);
  }

  @Test
  void shouldFindByToken() {
    final HttpServletRequest request = mock(HttpServletRequest.class);

    given(request.getCookies()).willReturn(new Cookie[] {new Cookie(JWT_REFRESH_COOKIE, TOKEN)});
    given(jwtUtils.validateJwtToken(TOKEN)).willReturn(TRUE);
    given(jwtUtils.getUserNameFromJwtToken(TOKEN)).willReturn(JONNY_EMAIL);
    given(userDetailsService.loadUserByUsername(JONNY_EMAIL)).willReturn(USER_DETAILS);
    given(jwtEncoder.encode(any())).willReturn(jwt);
    given(jwt.getTokenValue()).willReturn(TOKEN);
    given(jwtUtils.generateAccessJwtCookie(TOKEN)).willReturn(RESPONSE_COOKIE);

    final String actual = service.findByToken(request);
    assertThat(actual, is(RESPONSE_COOKIE.toString()));

    then(request).should(only()).getCookies();
    then(jwtUtils).should(times(1)).validateJwtToken(TOKEN);
    then(jwtUtils).should(times(1)).getUserNameFromJwtToken(TOKEN);
    then(userDetailsService).should(only()).loadUserByUsername(JONNY_EMAIL);
    then(jwtEncoder).should(only()).encode(any());
    then(jwt).should(only()).getTokenValue();
    then(jwtUtils).should(times(1)).generateAccessJwtCookie(TOKEN);
  }

  @Test
  void shouldNotFindByToken() {
    final HttpServletRequest request = mock(HttpServletRequest.class);

    given(request.getCookies()).willReturn(new Cookie[] {});

    final RuntimeException actual =
        assertThrows(IllegalArgumentException.class, () -> service.findByToken(request));
    assertThat(actual.getMessage(), is(NOT_FOUND_REFRESH_TOKEN));

    then(request).should(only()).getCookies();
  }

  @Test
  void shouldCleanAccessToken() {
    given(jwtUtils.cleanAccessToken()).willReturn(ResponseCookie.from("jwtCookie", "null").build());

    final String actual = service.cleanAccessToken();
    assertThat(actual, is("jwtCookie=null"));

    then(jwtUtils).should(only()).cleanAccessToken();
  }

  @Test
  void shouldCleanRefreshToken() {
    given(jwtUtils.cleanRefreshToken())
        .willReturn(ResponseCookie.from(JWT_REFRESH_COOKIE, "null").build());

    final String actual = service.cleanRefreshToken();
    assertThat(actual, is(JWT_REFRESH_COOKIE + "=null"));

    then(jwtUtils).should(only()).cleanRefreshToken();
  }

  @Test
  void shouldReturnExceptionWhenAuthenticationManagerReturnError() {
    final UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(JONNY_EMAIL, DEFAULT_PASSWORD);
    given(authManager.authenticate(authenticationToken)).willThrow(EXCEPTION);

    final Exception actual =
        assertThrows(RuntimeException.class, () -> service.generateAccessToken(loginRequest()));
    assertThat(actual, is(EXCEPTION));

    then(authManager).should(only()).authenticate(authenticationToken);
  }

  @Test
  void shouldReturnExceptionWhenJwtEncoderReturnError() {
    final UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(JONNY_EMAIL, DEFAULT_PASSWORD);
    final Authentication auth = mock(Authentication.class);

    given(authManager.authenticate(authenticationToken)).willReturn(auth);
    given(userDetailsService.loadUserByUsername(JONNY_EMAIL)).willReturn(USER_DETAILS);
    given(jwtEncoder.encode(any())).willThrow(EXCEPTION);

    final Exception actual =
        assertThrows(RuntimeException.class, () -> service.generateAccessToken(loginRequest()));
    assertThat(actual, is(EXCEPTION));

    then(authManager).should(only()).authenticate(authenticationToken);
    then(userDetailsService).should(only()).loadUserByUsername(JONNY_EMAIL);
    then(jwtEncoder).should(only()).encode(any());
  }

  @Test
  void shouldReturnExceptionWhenUserDetailsServiceReturnError() {
    final UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(JONNY_EMAIL, DEFAULT_PASSWORD);
    final Authentication auth = mock(Authentication.class);

    given(authManager.authenticate(authenticationToken)).willReturn(auth);
    given(userDetailsService.loadUserByUsername(JONNY_EMAIL)).willThrow(EXCEPTION);

    final Exception actual =
        assertThrows(RuntimeException.class, () -> service.generateAccessToken(loginRequest()));
    assertThat(actual, is(EXCEPTION));

    then(authManager).should(only()).authenticate(authenticationToken);
    then(userDetailsService).should(only()).loadUserByUsername(JONNY_EMAIL);
  }

  @Test
  void shouldReturnExceptionWhenJwtUtilsReturnError() {
    given(jwtUtils.cleanAccessToken()).willThrow(EXCEPTION);

    final Exception actual = assertThrows(RuntimeException.class, () -> service.cleanAccessToken());
    assertThat(actual, is(EXCEPTION));

    then(jwtUtils).should(only()).cleanAccessToken();
  }
}
