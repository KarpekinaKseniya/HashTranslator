package tt.authorization.domain.entity;

import java.util.Set;

public class User {
    private Long id;
    private String firstname;
    private String lastname;
    private Set<Roles> roles;
    private String email;
    private String password;
    private String salt;
}
