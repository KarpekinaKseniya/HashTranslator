package tt.authorization.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import tt.authorization.service.UserService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.only;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.ResponseEntity.ok;
import static tt.authorization.helper.UserHelper.createUserRequest;
import static tt.authorization.helper.UserHelper.userInfoResponse;

class AccountResourceTest {

  private static final Long ID = 3L;

  @Mock private UserService userService;
  @InjectMocks private AccountResource accountResource;

  @BeforeEach
  void setup() {
    openMocks(this);
  }

  @Test
  void shouldReturnUserInfo() {
    given(userService.getUserInfo(ID)).willReturn(userInfoResponse());

    assertThat(accountResource.getUserInfo(ID), is(ok(userInfoResponse())));

    then(userService).should(only()).getUserInfo(ID);
  }

  @Test
  void shouldDeleteUserById() {
    willDoNothing().given(userService).delete(ID);

    assertThat(accountResource.delete(ID), is(new ResponseEntity<>(NO_CONTENT)));

    then(userService).should(only()).delete(ID);
  }

  @Test
  void shouldCreateUser() {
    given(userService.createUser(createUserRequest())).willReturn(ID);

    assertThat(
        accountResource.createUser(createUserRequest()), is(new ResponseEntity<>(ID, CREATED)));

    then(userService).should(only()).createUser(createUserRequest());
  }
}
