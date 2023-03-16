package tt.authorization.transformer;

import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tt.authorization.domain.entity.Roles;
import tt.authorization.domain.entity.User;
import tt.authorization.domain.request.CreateUserRequest;
import tt.authorization.domain.response.UserInfoResponse;

@Component
public class UserTransformerImpl implements UserTransformer {

  private final PasswordEncoder encoder;

  public UserTransformerImpl(final PasswordEncoder encoder) {
    this.encoder = encoder;
  }

  @Override
  public User userRequestToEntity(final CreateUserRequest request, final Set<Roles> roles) {
    if (request == null && roles == null) {
      return null;
    }
    User user = new User();
    if (request != null) {
      user.setFirstname(request.getFirstname());
      user.setLastname(request.getLastname());
      user.setEmail(request.getEmail());
      user.setPassword(encoder.encode(request.getPassword()));
    }
    if (roles != null) {
      user.setRoles(roles);
    }
    return user;
  }

  @Override
  public UserInfoResponse entityToResponse(final User user) {
    if (user == null) {
      return null;
    }
    UserInfoResponse response = new UserInfoResponse();
    response.setFirstname(user.getFirstname());
    response.setLastname(user.getLastname());
    response.setEmail(user.getEmail());
    response.setRoles(user.getRoles());
    return response;
  }
}
