package tt.authorization.service.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static tt.authorization.domain.entity.ERole.ROLE_USER;
import static tt.authorization.helper.UserHelper.ENCODE_PASSWORD;
import static tt.authorization.helper.UserHelper.JONNY_EMAIL;
import static tt.authorization.helper.UserHelper.userEntityBuilder;

class UserDetailsTest {

  private static final Long ID = 3L;

  private UserDetailsImpl userDetails;

  @BeforeEach
  void setUp() {
    userDetails = UserDetailsImpl.build(userEntityBuilder().id(ID).build());
  }

  @Test
  void shouldGetId() {
    assertThat(userDetails.getId(), is(ID));
  }

  @Test
  void shouldGetEmail() {
    assertThat(userDetails.getUsername(), is(JONNY_EMAIL));
  }

  @Test
  void shouldGetPassword() {
    assertThat(userDetails.getPassword(), is(ENCODE_PASSWORD));
  }

  @Test
  void shouldGetAuthorized() {
    assertThat(
        userDetails.getAuthorities(), is(Set.of(new SimpleGrantedAuthority(ROLE_USER.name()))));
  }
}
