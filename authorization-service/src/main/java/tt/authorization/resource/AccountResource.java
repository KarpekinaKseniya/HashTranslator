package tt.authorization.resource;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tt.authorization.domain.request.ResetPasswordRequest;
import tt.authorization.domain.response.UserInfoResponse;
import tt.authorization.service.UserService;

//TODO
public class AccountResource {
    private final UserService userService;

    public AccountResource(final UserService userService) {
        this.userService = userService;
    }

    public ResponseEntity<UserInfoResponse> getUserInfo(final Long id) {
        throw new NotImplementedException();
    }

    public ResponseEntity<HttpStatus> delete(final Long id) {
        throw new NotImplementedException();
    }

    public ResponseEntity<HttpStatus> changePassword(final ResetPasswordRequest resetPasswordRequest) {
        throw new NotImplementedException();
    }
}
