package tt.authorization.integration_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import tt.authorization.AuthorizationApplication;
import tt.authorization.domain.request.LoginRequest;
import tt.authorization.integration_tests.config.AuthHelper;
import tt.authorization.integration_tests.config.HSQLConfig;

import javax.servlet.http.Cookie;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static tt.authorization.helper.UserHelper.DEFAULT_PASSWORD;
import static tt.authorization.helper.UserHelper.JONNY_EMAIL;
import static tt.authorization.helper.UserHelper.loginRequest;

@SpringBootTest(
    classes = {AuthorizationApplication.class},
    webEnvironment = RANDOM_PORT)
@TestPropertySource(locations = {"classpath:/application-test.properties"})
@AutoConfigureMockMvc
@Import(HSQLConfig.class)
@EnableConfigurationProperties
public class AuthIT {

  @Value("${local.server.port}")
  private int port;

  @Value("${app.jwtCookieName}")
  private String jwtCookieName;

  @Value("${app.jwtRefreshCookieName}")
  private String jwtRefreshCookieName;

  @Value("${app.jwtCookieAgeSec}")
  private int jwtCookieAgeSec;

  @Autowired private MockMvc mockMvc;

  private AuthHelper testHelper;

  @BeforeEach
  void setUp() {
    testHelper = new AuthHelper(mockMvc);
  }

  @Test
  @Sql(
      executionPhase = BEFORE_TEST_METHOD,
      scripts = {"classpath:integration/db/db_cleanup.sql", "classpath:integration/db/db_data.sql"})
  void shouldReturnTokensWhenLogin() throws Exception {
    final MockHttpServletResponse cookies = testHelper.loginAndReturnToken(loginRequest(), port);
    final Cookie jwtCookie = cookies.getCookie(jwtCookieName);
    final Cookie refreshCookie = cookies.getCookie(jwtRefreshCookieName);
    assertThat(jwtCookie, notNullValue());
    assertThat(jwtCookie.getMaxAge(), is(jwtCookieAgeSec));
    assertThat(jwtCookie.getValue(), notNullValue());
    assertThat(refreshCookie, notNullValue());
    assertThat(refreshCookie.getMaxAge(), is(jwtCookieAgeSec));
    assertThat(refreshCookie.getValue(), notNullValue());
  }

  @Test
  void shouldNotLoginWithWrongEmailThrowsException() throws Exception {
    testHelper
        .login(
            LoginRequest.builder().email("wrong@mail.ru").password(DEFAULT_PASSWORD).build(), port)
        .andExpect(jsonPath("$.statusCode", equalTo(SC_UNAUTHORIZED)))
        .andExpect(jsonPath("$.message", equalTo("Bad credentials")))
        .andExpect(jsonPath("$.description", notNullValue()));
  }

  @Test
  void shouldNotLoginWithWrongPasswordThrowsException() throws Exception {
    testHelper
        .login(LoginRequest.builder().email(JONNY_EMAIL).password("A#!g$|w^]9$zJ").build(), port)
        .andExpect(jsonPath("$.statusCode", equalTo(SC_UNAUTHORIZED)))
        .andExpect(jsonPath("$.message", equalTo("Bad credentials")))
        .andExpect(jsonPath("$.description", notNullValue()));
  }

  @Test
  void shouldNotLoginWithNotValidEmailAndPasswordThrowsException() throws Exception {
    testHelper
        .login(LoginRequest.builder().build(), port)
        .andExpect(jsonPath("$.statusCode", equalTo(SC_BAD_REQUEST)))
        .andExpect(jsonPath("$.message", equalTo("Email must not be blank, Password must not be blank")))
        .andExpect(jsonPath("$.description", notNullValue()));
  }

  @Test
  void shouldLogout() throws Exception {
    final MockHttpServletResponse response =
        mockMvc
            .perform(post(testHelper.buildRequestUrlStr(port) + "/auth/logout"))
            .andReturn()
            .getResponse();
    final Cookie jwtCookie = response.getCookie(jwtCookieName);
    final Cookie refreshCookie = response.getCookie(jwtRefreshCookieName);
    assertThat(jwtCookie.getValue(), emptyString());
    assertThat(refreshCookie.getValue(), emptyString());
  }

  @Test
  @Sql(
      executionPhase = BEFORE_TEST_METHOD,
      scripts = {"classpath:integration/db/db_cleanup.sql", "classpath:integration/db/db_data.sql"})
  void shouldRefreshToken() {
    given()
        .cookies(testHelper.loginAndReturnCookies(loginRequest(), port))
        .post(testHelper.buildRequestUrlStr(port) + "/auth/token/refresh")
        .then()
        .assertThat()
        .statusCode(SC_OK);
  }
}
