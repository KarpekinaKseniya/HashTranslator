package tt.authorization.integration_tests.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Cookies;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tt.authorization.domain.request.LoginRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class AuthHelper {

  private final MockMvc mockMvc;

  public AuthHelper(final MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  public String buildRequestUrlStr(final int port) {
    return "http://localhost:" + port + "/api/v1";
  }

  public String getResponse(String file) throws IOException {
    return new String(
        Files.readAllBytes(Paths.get("src/test/resources/integration/response/" + file)));
  }

  public String getRequest(String file) throws IOException {
    return new String(
        Files.readAllBytes(Paths.get("src/test/resources/integration/request/" + file)));
  }

  public ResultActions login(final LoginRequest request, final int port) throws Exception {
    return mockMvc.perform(
        post(buildRequestUrlStr(port) + "/auth/login")
            .content(new ObjectMapper().writeValueAsString(request))
            .contentType(APPLICATION_JSON));
  }

  public MockHttpServletResponse loginAndReturnToken(final LoginRequest request, final int port)
      throws Exception {
    return mockMvc
        .perform(
            post(buildRequestUrlStr(port) + "/auth/login")
                .content(new ObjectMapper().writeValueAsString(request))
                .contentType(APPLICATION_JSON))
        .andReturn()
        .getResponse();
  }

  public Cookies loginAndReturnCookies(final LoginRequest request, final int port) {
    return given()
        .contentType(JSON)
        .when()
        .body(request)
        .post(buildRequestUrlStr(port) + "/auth/login")
        .then()
        .statusCode(200)
        .extract()
        .response()
        .getDetailedCookies();
  }
}
