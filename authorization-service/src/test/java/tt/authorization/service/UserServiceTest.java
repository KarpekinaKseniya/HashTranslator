package tt.authorization.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import tt.authorization.domain.response.UserInfoResponse;
import tt.authorization.exception.EntityNotFoundException;
import tt.authorization.repository.RoleRepository;
import tt.authorization.repository.UserRepository;
import tt.authorization.transformer.UserTransformer;

import java.util.Optional;
import java.util.Set;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;
import static tt.authorization.domain.entity.ERole.ROLE_USER;
import static tt.authorization.helper.UserHelper.JONNY_EMAIL;
import static tt.authorization.helper.UserHelper.createUserRequest;
import static tt.authorization.helper.UserHelper.defaultRole;
import static tt.authorization.helper.UserHelper.userEntityBuilder;
import static tt.authorization.helper.UserHelper.userInfoResponse;

class UserServiceTest {

    private static final Long ID = 2L;
    private static final String USER_NOT_FOUND_ERROR = format("User with id = %s didn't found.", ID);
    private static final String ROLE_NOT_FOUND_ERROR = "Role with name = ROLE_USER didn't found.";
    private static final String EMAIL_EXISTS_ERROR = "Error: email is already in use!";
    private static final Exception EXCEPTION = new RuntimeException("some error message");

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserTransformer userTransformer;
    @Mock
    private RoleRepository roleRepository;
    private UserService service;

    @BeforeEach
    void setup() {
        openMocks(this);
        service = new UserServiceImpl(userRepository, userTransformer, roleRepository);
    }

    @AfterEach
    void verify() {
        verifyNoMoreInteractions(userRepository, userTransformer, roleRepository);
    }

    @Test
    void shouldGetUserInfo() {
        given(userRepository.findById(ID)).willReturn(Optional.of(userEntityBuilder().id(ID).build()));
        given(userTransformer.entityToResponse(userEntityBuilder().id(ID).build()))
                .willReturn(userInfoResponse());

        final UserInfoResponse actual = service.getUserInfo(ID);
        assertThat(actual, is(userInfoResponse()));

        then(userRepository).should(only()).findById(ID);
        then(userTransformer).should(only()).entityToResponse(userEntityBuilder().id(ID).build());
    }

    @Test
    void shouldReturnExceptionWhenGetUserInfoByWrongId() {
        given(userRepository.findById(ID)).willReturn(Optional.empty());

        final RuntimeException actual =
                assertThrows(EntityNotFoundException.class, () -> service.getUserInfo(ID));
        assertThat(actual.getMessage(), is(USER_NOT_FOUND_ERROR));

        then(userRepository).should(only()).findById(ID);
    }

    @Test
    void shouldReturnErrorWhenUserRepositoryReturnError() {
        given(userRepository.findById(ID)).willThrow(EXCEPTION);

        final Exception actual = assertThrows(RuntimeException.class, () -> service.getUserInfo(ID));
        assertThat(actual, is(EXCEPTION));

        then(userRepository).should(only()).findById(ID);
    }

    @Test
    void shouldReturnErrorWhenUserTransformerReturnError() {
        given(userRepository.findById(ID)).willReturn(Optional.of(userEntityBuilder().id(ID).build()));
        given(userTransformer.entityToResponse(userEntityBuilder().id(ID).build()))
                .willThrow(EXCEPTION);

        final Exception actual = assertThrows(RuntimeException.class, () -> service.getUserInfo(ID));
        assertThat(actual, is(EXCEPTION));

        then(userRepository).should(only()).findById(ID);
        then(userTransformer).should(only()).entityToResponse(userEntityBuilder().id(ID).build());
    }

    @Test
    void shouldDeleteSuccess() {
        given(userRepository.existsById(ID)).willReturn(TRUE);
        willDoNothing().given(userRepository).deleteById(ID);

        service.delete(ID);

        then(userRepository).should(times(1)).existsById(ID);
        then(userRepository).should(times(1)).deleteById(ID);
    }

    @Test
    void shouldReturnExceptionWhenDeleteWithNotExistsId() {
        given(userRepository.existsById(ID)).willReturn(FALSE);

        final RuntimeException actual =
                assertThrows(EntityNotFoundException.class, () -> service.delete(ID));
        assertThat(actual.getMessage(), is(USER_NOT_FOUND_ERROR));

        then(userRepository).should(only()).existsById(ID);
    }

    @Test
    void shouldCreateUserSuccess() {
        given(userRepository.existsByEmail(JONNY_EMAIL)).willReturn(FALSE);
        given(roleRepository.findByName(ROLE_USER)).willReturn(Optional.of(defaultRole()));
        given(userTransformer.userRequestToEntity(createUserRequest(), Set.of(defaultRole())))
                .willReturn(userEntityBuilder().build());
        given(userRepository.save(userEntityBuilder().build()))
                .willReturn(userEntityBuilder().id(ID).build());

        final Long actual = service.createUser(createUserRequest());
        assertThat(actual, is(ID));

        then(userRepository).should(times(1)).existsByEmail(JONNY_EMAIL);
        then(roleRepository).should(only()).findByName(ROLE_USER);
        then(userTransformer)
                .should(only())
                .userRequestToEntity(createUserRequest(), Set.of(defaultRole()));
        then(userRepository).should(times(1)).save(userEntityBuilder().build());
    }

    @Test
    void shouldNotCreateUserWhenEmailExists() {
        given(userRepository.existsByEmail(JONNY_EMAIL)).willReturn(TRUE);

        final RuntimeException actual =
                assertThrows(IllegalArgumentException.class, () -> service.createUser(createUserRequest()));
        assertThat(actual.getMessage(), is(EMAIL_EXISTS_ERROR));

        then(userRepository).should(only()).existsByEmail(JONNY_EMAIL);
    }

    @Test
    void shouldNotCreateUserWhenUserRoleNotFoundInDB() {
        given(userRepository.existsByEmail(JONNY_EMAIL)).willReturn(FALSE);
        given(roleRepository.findByName(ROLE_USER)).willReturn(Optional.empty());

        final RuntimeException actual =
                assertThrows(EntityNotFoundException.class, () -> service.createUser(createUserRequest()));
        assertThat(actual.getMessage(), is(ROLE_NOT_FOUND_ERROR));

        then(userRepository).should(only()).existsByEmail(JONNY_EMAIL);
        then(roleRepository).should(only()).findByName(ROLE_USER);
    }

    @Test
    void shouldReturnErrorWhenRoleRepositoryReturnError() {
        given(userRepository.existsByEmail(JONNY_EMAIL)).willReturn(FALSE);
        given(roleRepository.findByName(ROLE_USER)).willThrow(EXCEPTION);

        final Exception actual =
                assertThrows(RuntimeException.class, () -> service.createUser(createUserRequest()));
        assertThat(actual, is(EXCEPTION));

        then(userRepository).should(only()).existsByEmail(JONNY_EMAIL);
        then(roleRepository).should(only()).findByName(ROLE_USER);
    }
}