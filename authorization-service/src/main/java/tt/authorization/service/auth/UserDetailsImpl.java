package tt.authorization.service.auth;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import tt.authorization.domain.entity.User;

public class UserDetailsImpl extends org.springframework.security.core.userdetails.User {

  private static final long serialVersionUID = 1L;
  private Long id;

  public UserDetailsImpl(
      final Long id,
      final String email,
      final String password,
      final Collection<? extends GrantedAuthority> authorities) {
    super(email, password, authorities);
    this.id = id;
  }

  public static UserDetailsImpl build(final User user) {
    final List<GrantedAuthority> authorities =
        user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName().name()))
            .collect(toList());

    return new UserDetailsImpl(user.getId(), user.getEmail(), user.getPassword(), authorities);
  }

  public Long getId() {
    return id;
  }
}
