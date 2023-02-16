package tt.authorization.service;

import tt.authorization.domain.request.RegistrationRequest;
import tt.authorization.domain.request.ResetPasswordRequest;
import tt.authorization.domain.response.UserInfoResponse;

//TODO
public interface UserService {

    UserInfoResponse getUserInfo(Long id);

    void delete(Long id);

    void changePassword(ResetPasswordRequest resetPasswordRequest);

    Long register(RegistrationRequest request);
}
