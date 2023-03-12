package tt.authorization.service;

import tt.authorization.domain.request.CreateUserRequest;
import tt.authorization.domain.response.UserInfoResponse;

public interface UserService {

  UserInfoResponse getUserInfo(Long id);

  void delete(Long id);

  Long createUser(CreateUserRequest request);

}
