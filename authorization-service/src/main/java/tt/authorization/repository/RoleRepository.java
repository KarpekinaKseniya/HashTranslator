package tt.authorization.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tt.authorization.domain.entity.ERole;
import tt.authorization.domain.entity.Roles;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> {

  Optional<Roles> findByName(ERole name);
}
