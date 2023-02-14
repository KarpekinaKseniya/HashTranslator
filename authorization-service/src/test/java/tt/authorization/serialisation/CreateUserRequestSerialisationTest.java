package tt.authorization.serialisation;

import org.junit.jupiter.api.BeforeEach;
import tt.authorization.domain.request.CreateUserRequest;
import tt.authorization.helper.UserHelper;

public class CreateUserRequestSerialisationTest extends JsonTestBase<CreateUserRequest> {

  @BeforeEach
  void beforeEach() {
    expected = UserHelper::createUserRequest;
    fileName = "expected_user_request.json";
    expectedType = CreateUserRequest.class;
  }
}
