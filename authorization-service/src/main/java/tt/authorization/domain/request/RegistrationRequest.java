package tt.authorization.domain.request;

import java.util.Set;
import tt.authorization.domain.entity.ERole;

public class RegistrationRequest {
    private String firstname;
    private String lastname;
    private Set<ERole> roles;
    private String email;
    private String password;
}
