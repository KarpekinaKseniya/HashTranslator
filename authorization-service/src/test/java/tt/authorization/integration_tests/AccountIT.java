package tt.authorization.integration_tests;

import io.restassured.http.Cookies;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import tt.authorization.AuthorizationApplication;
import tt.authorization.domain.request.LoginRequest;
import tt.authorization.integration_tests.config.AuthHelper;
import tt.authorization.integration_tests.config.HSQLConfig;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonStringEquals;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static tt.authorization.helper.UserHelper.loginRequest;

@SpringBootTest(
    classes = {AuthorizationApplication.class},
    webEnvironment = RANDOM_PORT)
@TestPropertySource(locations = {"classpath:/application-test.properties"})
@AutoConfigureMockMvc
@Import(HSQLConfig.class)
@EnableConfigurationProperties
public class AccountIT {

  private static final Long ID = 1L;

  @Value("${local.server.port}")
  private int port;

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
  void shouldReturnForbidden() {
    final Cookies cookies = testHelper.loginAndReturnCookies(loginRequest(), port);
    given()
        .cookies(cookies)
        .contentType(JSON)
        .accept(JSON)
        .pathParam("id", ID)
        .delete(testHelper.buildRequestUrlStr(port) + "/account/{id}")
        .then()
        .assertThat()
        .statusCode(SC_FORBIDDEN);
  }

  @Test
  @Sql(
      executionPhase = BEFORE_TEST_METHOD,
      scripts = {"classpath:integration/db/db_cleanup.sql", "classpath:integration/db/db_data.sql"})
  void shouldDeleteUserSuccess() {
    final LoginRequest request =
        LoginRequest.builder().email("black34@gmail.eu").password("adminpassword").build();
    final Cookies cookies = testHelper.loginAndReturnCookies(request, port);
    given()
        .cookies(cookies)
        .contentType(JSON)
        .accept(JSON)
        .pathParam("id", ID)
        .delete(testHelper.buildRequestUrlStr(port) + "/account/{id}")
        .then()
        .assertThat()
        .statusCode(SC_NO_CONTENT);
  }

  @Test
  @Sql(
      executionPhase = BEFORE_TEST_METHOD,
      scripts = {"classpath:integration/db/db_cleanup.sql", "classpath:integration/db/db_data.sql"})
  void shouldReturnNotFoundWhenDeleteUser() throws IOException {
    final LoginRequest request =
        LoginRequest.builder().email("black34@gmail.eu").password("adminpassword").build();
    final Cookies cookies = testHelper.loginAndReturnCookies(request, port);
    given()
        .cookies(cookies)
        .contentType(JSON)
        .accept(JSON)
        .pathParam("id", 110L)
        .delete(testHelper.buildRequestUrlStr(port) + "/account/{id}")
        .then()
        .assertThat()
        .statusCode(SC_NOT_FOUND)
        .body(jsonStringEquals(testHelper.getResponse("delete_user_not_found.json")));
  }

  @Test
  @Sql(
      executionPhase = BEFORE_TEST_METHOD,
      scripts = {"classpath:integration/db/db_cleanup.sql", "classpath:integration/db/db_data.sql"})
  void shouldReturnNotFoundWhenGetUserInfo() throws IOException {
    final LoginRequest request =
        LoginRequest.builder().email("black34@gmail.eu").password("adminpassword").build();
    final Cookies cookies = testHelper.loginAndReturnCookies(request, port);
    given()
        .cookies(cookies)
        .contentType(JSON)
        .accept(JSON)
        .pathParam("id", 110L)
        .get(testHelper.buildRequestUrlStr(port) + "/account/{id}")
        .then()
        .assertThat()
        .statusCode(SC_NOT_FOUND)
        .body(jsonStringEquals(testHelper.getResponse("get_user_not_found.json")));
  }

  @Test
  @Sql(
      executionPhase = BEFORE_TEST_METHOD,
      scripts = {"classpath:integration/db/db_cleanup.sql", "classpath:integration/db/db_data.sql"})
  void shouldGetCurrentUserInfoSuccess() throws IOException {
    final Cookies cookies = testHelper.loginAndReturnCookies(loginRequest(), port);
    given()
        .cookies(cookies)
        .contentType(JSON)
        .accept(JSON)
        .pathParam("id", ID)
        .get(testHelper.buildRequestUrlStr(port) + "/account/{id}")
        .then()
        .assertThat()
        .statusCode(SC_OK)
        .body(jsonStringEquals(testHelper.getResponse("get_user_info_success.json")));
  }

  @Test
  @Sql(
      executionPhase = BEFORE_TEST_METHOD,
      scripts = {"classpath:integration/db/db_cleanup.sql", "classpath:integration/db/db_data.sql"})
  void shouldGetUserInfoWhenAdminSuccess() throws IOException {
    final LoginRequest request =
        LoginRequest.builder().email("black34@gmail.eu").password("adminpassword").build();
    final Cookies cookies = testHelper.loginAndReturnCookies(request, port);
    given()
        .cookies(cookies)
        .contentType(JSON)
        .accept(JSON)
        .pathParam("id", ID)
        .get(testHelper.buildRequestUrlStr(port) + "/account/{id}")
        .then()
        .assertThat()
        .statusCode(SC_OK)
        .body(jsonStringEquals(testHelper.getResponse("get_user_info_success.json")));
  }

  @Test
  @Sql(
      executionPhase = BEFORE_TEST_METHOD,
      scripts = {"classpath:integration/db/db_cleanup.sql", "classpath:integration/db/db_data.sql"})
  void shouldCreateUserSuccess() throws Exception {
    final LoginRequest request =
        LoginRequest.builder().email("black34@gmail.eu").password("adminpassword").build();
    final Cookies cookies = testHelper.loginAndReturnCookies(request, port);
    given()
        .cookies(cookies)
        .contentType(JSON)
        .accept(JSON)
        .body(testHelper.getRequest("create_user_success.json"))
        .post(testHelper.buildRequestUrlStr(port) + "/account")
        .then()
        .assertThat()
        .statusCode(SC_CREATED);
  }

  @Test
  @Sql(
      executionPhase = BEFORE_TEST_METHOD,
      scripts = {"classpath:integration/db/db_cleanup.sql", "classpath:integration/db/db_data.sql"})
  void shouldNotCreateUserWithExistsEmail() throws Exception {
    final LoginRequest request =
        LoginRequest.builder().email("black34@gmail.eu").password("adminpassword").build();
    final Cookies cookies = testHelper.loginAndReturnCookies(request, port);

    given()
        .cookies(cookies)
        .contentType(JSON)
        .accept(JSON)
        .body(testHelper.getRequest("create_user_exists_email.json"))
        .post(testHelper.buildRequestUrlStr(port) + "/account")
        .then()
        .assertThat()
        .statusCode(SC_BAD_REQUEST)
        .body(jsonStringEquals(testHelper.getResponse("create_user_exists_email_error.json")));
  }

  @Test
  @Sql(
      executionPhase = BEFORE_TEST_METHOD,
      scripts = {"classpath:integration/db/db_cleanup.sql", "classpath:integration/db/db_data.sql"})
  void shouldNotCreateUserWithInvalidRequest() throws Exception {
    final LoginRequest request =
        LoginRequest.builder().email("black34@gmail.eu").password("adminpassword").build();
    final Cookies cookies = testHelper.loginAndReturnCookies(request, port);

    given()
        .cookies(cookies)
        .contentType(JSON)
        .accept(JSON)
        .body(testHelper.getRequest("create_user_invalid_data.json"))
        .post(testHelper.buildRequestUrlStr(port) + "/account")
        .then()
        .assertThat()
        .statusCode(SC_BAD_REQUEST)
        .body(jsonStringEquals(testHelper.getResponse("create_user_bad_request.json")));
  }
}
