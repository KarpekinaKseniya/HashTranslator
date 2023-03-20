package tt.authorization.service.auth;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tt.authorization.domain.entity.User;
import tt.authorization.repository.UserRepository;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
    final User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () -> new UsernameNotFoundException("User Not Found with email: " + email));

    return UserDetailsImpl.build(user);
  }
}
