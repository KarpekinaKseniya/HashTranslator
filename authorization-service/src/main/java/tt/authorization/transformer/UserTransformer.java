package tt.authorization.transformer;

import java.util.Set;
import tt.authorization.domain.entity.Roles;
import tt.authorization.domain.entity.User;
import tt.authorization.domain.request.CreateUserRequest;
import tt.authorization.domain.response.UserInfoResponse;

public interface UserTransformer {

  User userRequestToEntity(CreateUserRequest request, Set<Roles> roles);

  UserInfoResponse entityToResponse(User user);
}
