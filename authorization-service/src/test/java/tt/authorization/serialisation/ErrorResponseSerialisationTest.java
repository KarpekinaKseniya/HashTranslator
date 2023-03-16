package tt.authorization.serialisation;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

import org.junit.jupiter.api.BeforeEach;
import tt.authorization.domain.response.ErrorResponse;

public class ErrorResponseSerialisationTest extends JsonTestBase<ErrorResponse> {

  @BeforeEach
  void beforeEach() {
    expected =
        () ->
            ErrorResponse.builder()
                .statusCode(SC_FORBIDDEN)
                .message("error message")
                .description("error description")
                .build();
    fileName = "expected_error_response.json";
    expectedType = ErrorResponse.class;
  }
}
