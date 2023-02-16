package tt.authorization.repository;

import java.util.Optional;
import tt.authorization.domain.entity.User;

//TODO
public interface UserRepository {

    Long save(User user);

    void delete(Long id);

    Optional<User> getById(Long id);

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
