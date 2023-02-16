package tt.authorization.domain.response;

import java.util.Set;
import tt.authorization.domain.entity.Roles;

public class UserInfoResponse {
    private String firstname;
    private String lastname;
    private Set<Roles> roles;
    private String email;
}
