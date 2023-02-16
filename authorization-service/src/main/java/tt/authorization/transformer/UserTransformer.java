package tt.authorization.transformer;

import tt.authorization.domain.entity.User;
import tt.authorization.domain.request.RegistrationRequest;
import tt.authorization.domain.response.UserInfoResponse;

//TODO
public interface UserTransformer {

    User singUpToEntity(RegistrationRequest request);

    UserInfoResponse entityToResponse(User user);
}
