package tt.authorization.serialisation;

import org.junit.jupiter.api.BeforeEach;
import tt.authorization.domain.request.LoginRequest;
import tt.authorization.helper.UserHelper;

public class LoginRequestSerialisationTest extends JsonTestBase<LoginRequest> {

  @BeforeEach
  void beforeEach() {
    expected = UserHelper::loginRequest;
    fileName = "expected_login_request.json";
    expectedType = LoginRequest.class;
  }
}
