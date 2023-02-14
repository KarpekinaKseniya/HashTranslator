package tt.authorization.serialisation;

import org.junit.jupiter.api.BeforeEach;
import tt.authorization.domain.response.UserInfoResponse;
import tt.authorization.helper.UserHelper;

public class UserInfoResponseSerialisationTest extends JsonTestBase<UserInfoResponse> {

    @BeforeEach
    void beforeEach() {
        expected = UserHelper::userInfoResponse;
        fileName = "expected_user_info_response.json";
        expectedType = UserInfoResponse.class;
    }
}
