package tt.authorization.resource;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import tt.authorization.domain.request.LoginRequest;
import tt.authorization.domain.request.RegistrationRequest;
import tt.authorization.service.UserService;

//TODO
public class AuthResource {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthResource(final AuthenticationManager authenticationManager, final UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    public ResponseEntity<?> auth(final LoginRequest request) {
        throw new NotImplementedException();
    }

    public ResponseEntity<?> register(final RegistrationRequest request) {
        throw new NotImplementedException();
    }

    public ResponseEntity<?> logoutUser() {
        throw new NotImplementedException();
    }
}
