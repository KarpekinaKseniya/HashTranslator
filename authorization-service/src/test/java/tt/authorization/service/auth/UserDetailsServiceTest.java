package tt.authorization.service.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tt.authorization.domain.entity.User;
import tt.authorization.repository.UserRepository;

import java.util.Optional;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;
import static tt.authorization.helper.UserHelper.JONNY_EMAIL;
import static tt.authorization.helper.UserHelper.userEntityBuilder;

class UserDetailsServiceTest {

  private static final User USER = userEntityBuilder().id(5L).build();
  private static final Exception EXCEPTION = new RuntimeException("some exception");

  @Mock private UserRepository userRepository;

  private UserDetailsService service;

  @BeforeEach
  void setup() {
    openMocks(this);
    service = new UserDetailsServiceImpl(userRepository);
  }

  @AfterEach
  void verify() {
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void shouldLoadUserByUsername() {
    given(userRepository.findByEmail(JONNY_EMAIL)).willReturn(Optional.of(USER));

    final UserDetails actual = service.loadUserByUsername(JONNY_EMAIL);
    assertThat(actual, is(UserDetailsImpl.build(USER)));

    then(userRepository).should(only()).findByEmail(JONNY_EMAIL);
  }

  @Test
  void shouldNotLoadUserWhenUsernameNotFound() {
    given(userRepository.findByEmail(JONNY_EMAIL)).willReturn(Optional.empty());

    final AuthenticationException actual =
        assertThrows(
            UsernameNotFoundException.class, () -> service.loadUserByUsername(JONNY_EMAIL));
    assertThat(actual.getMessage(), is(format("User Not Found with email: %s", JONNY_EMAIL)));

    then(userRepository).should(only()).findByEmail(JONNY_EMAIL);
  }

  @Test
  void shouldReturnExceptionWhenUserRepositoryThrowsError() {
    given(userRepository.findByEmail(JONNY_EMAIL)).willThrow(EXCEPTION);

    final Exception actual =
        assertThrows(RuntimeException.class, () -> service.loadUserByUsername(JONNY_EMAIL));
    assertThat(actual, is(EXCEPTION));

    then(userRepository).should(only()).findByEmail(JONNY_EMAIL);
  }
}
