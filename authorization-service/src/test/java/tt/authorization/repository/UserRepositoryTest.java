package tt.authorization.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import tt.authorization.AuthorizationApplication;
import tt.authorization.domain.entity.User;
import tt.authorization.integration_tests.config.HSQLConfig;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static tt.authorization.helper.UserHelper.JONNY_EMAIL;
import static tt.authorization.helper.UserHelper.userEntityBuilder;

@Import(HSQLConfig.class)
@SpringBootTest(
    classes = {AuthorizationApplication.class},
    webEnvironment = RANDOM_PORT)
@TestPropertySource(locations = "classpath:/application-test.properties")
class UserRepositoryTest {

  private static final Long ID = 1L;

  @Autowired private UserRepository repository;

  @Test
  @Sql({"classpath:integration/db/db_cleanup.sql", "/integration/db/db_data.sql"})
  void shouldFindByEmailSuccess() {
    final Optional<User> actual = repository.findByEmail(JONNY_EMAIL);
    assertThat(actual, is(Optional.of(userEntityBuilder().id(ID).build())));
  }

  @Test
  @Sql({"classpath:integration/db/db_cleanup.sql"})
  void shouldFindByEmailSuccessReturnEmptyOptional() {
    final Optional<User> actual = repository.findByEmail(JONNY_EMAIL);
    assertThat(actual, is(Optional.empty()));
  }

  @Test
  @Sql({"classpath:integration/db/db_cleanup.sql", "/integration/db/db_data.sql"})
  void shouldExistsByEmail() {
    assertTrue(repository.existsByEmail(JONNY_EMAIL));
  }

  @Test
  @Sql({"classpath:integration/db/db_cleanup.sql"})
  void shouldNotExistsByEmail() {
    assertFalse(repository.existsByEmail(JONNY_EMAIL));
  }

  @Test
  @Sql({"classpath:integration/db/db_cleanup.sql", "/integration/db/db_data.sql"})
  void shouldFindByIdSuccess() {
    final Optional<User> actual = repository.findById(ID);
    assertThat(actual, is(Optional.of(userEntityBuilder().id(ID).build())));
  }

  @Test
  @Sql({"classpath:integration/db/db_cleanup.sql"})
  void shouldFindByIdSuccessReturnEmptyOptional() {
    final Optional<User> actual = repository.findById(ID);
    assertThat(actual, is(Optional.empty()));
  }

  @Test
  @Sql({"classpath:integration/db/db_cleanup.sql", "/integration/db/db_data.sql"})
  void shouldExistsById() {
    assertTrue(repository.existsById(ID));
  }

  @Test
  @Sql({"classpath:integration/db/db_cleanup.sql"})
  void shouldNotExistsById() {
    assertFalse(repository.existsById(ID));
  }

  @Test
  @Sql({"classpath:integration/db/db_cleanup.sql", "/integration/db/db_data.sql"})
  void shouldDeleteByIdSuccess() {
    assertThat(repository.count(), is(3L));

    repository.deleteById(ID);

    assertThat(repository.count(), is(2L));
  }

  @Test
  @Sql({"classpath:integration/db/db_cleanup.sql", "/integration/db/db_data.sql"})
  void shouldSaveUserSuccess() {
    assertThat(repository.count(), is(3L));

    final User save = repository.save(userEntityBuilder().email("other.address@mail.com").build());

    assertThat(repository.count(), is(4L));
    assertThat(save.getId(), notNullValue());
  }
}
