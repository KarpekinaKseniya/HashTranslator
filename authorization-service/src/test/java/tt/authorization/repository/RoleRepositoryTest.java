package tt.authorization.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static tt.authorization.domain.entity.ERole.ROLE_USER;
import static tt.authorization.helper.UserHelper.defaultRole;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import tt.authorization.AuthorizationApplication;
import tt.authorization.domain.entity.Roles;
import tt.authorization.integration_tests.config.HSQLConfig;

@Import(HSQLConfig.class)
@SpringBootTest(
    classes = {AuthorizationApplication.class},
    webEnvironment = RANDOM_PORT)
@TestPropertySource(locations = "classpath:/application-test.properties")
class RoleRepositoryTest {

  @Autowired private RoleRepository repository;

  @Test
  @Sql({"classpath:integration/db/db_cleanup.sql", "/integration/db/db_data.sql"})
  void shouldFindByNameSuccess() {
    final Optional<Roles> actual = repository.findByName(ROLE_USER);
    assertThat(actual, is(Optional.of(defaultRole())));
  }
}
