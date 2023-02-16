package tt.authorization.service.auth;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tt.authorization.repository.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //TODO
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        throw new NotImplementedException("Find user by email or else throw");
    }
}
