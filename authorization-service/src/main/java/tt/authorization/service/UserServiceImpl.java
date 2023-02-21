package tt.authorization.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tt.authorization.config.jwt.JwtUtils;
import tt.authorization.domain.entity.ERole;
import tt.authorization.domain.entity.Roles;
import tt.authorization.domain.entity.User;
import tt.authorization.domain.request.CreateUserRequest;
import tt.authorization.domain.request.LoginRequest;
import tt.authorization.domain.response.UserInfoResponse;
import tt.authorization.exception.EntityNotFoundException;
import tt.authorization.repository.RoleRepository;
import tt.authorization.repository.UserRepository;
import tt.authorization.service.auth.UserDetailsImpl;
import tt.authorization.transformer.UserTransformer;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;

@Service
public class UserServiceImpl implements UserService {

  private static final String USER_NOT_FOUND_ERROR = "User with id = %s didn't found.";
  private static final String ROLE_NOT_FOUND_ERROR = "Role with name = %s didn't found.";

  private final JwtUtils jwtUtils;
  private final UserRepository userRepository;
  private final UserTransformer userTransformer;
  private final AuthenticationManager authenticationManager;
  private final RoleRepository roleRepository;

  public UserServiceImpl(
      final JwtUtils jwtUtils,
      final UserRepository userRepository,
      final UserTransformer userTransformerImpl,
      final AuthenticationManager authenticationManager,
      final RoleRepository roleRepository) {
    this.jwtUtils = jwtUtils;
    this.userRepository = userRepository;
    this.userTransformer = userTransformerImpl;
    this.authenticationManager = authenticationManager;
    this.roleRepository = roleRepository;
  }

  @Override
  public UserInfoResponse getUserInfo(final Long id) {
    final Optional<User> user = userRepository.findById(id);
    return userTransformer.entityToResponse(
        user.orElseThrow(() -> new EntityNotFoundException(format(USER_NOT_FOUND_ERROR, id))));
  }

  @Override
  public void delete(final Long id) {
    if (userRepository.existsById(id)) {
      userRepository.deleteById(id);
    } else {
      throw new EntityNotFoundException(format(USER_NOT_FOUND_ERROR, id));
    }
  }

  @Override
  public Long createUser(final CreateUserRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("Error: email is already in use!");
    }
    final Set<Roles> roles = new HashSet<>();
    for (final ERole role : request.getRole()) {
      roles.add(
          roleRepository
              .findByName(role)
              .orElseThrow(
                  () -> new EntityNotFoundException(format(ROLE_NOT_FOUND_ERROR, role.name()))));
    }
    final User user = userTransformer.userRequestToEntity(request, roles);
    return userRepository.save(user).getId();
  }

  @Override
  public String login(final LoginRequest request) {
    final Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    final UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    return jwtUtils.generateJwtCookie(userDetails).toString();
  }

  @Override
  public String logout() {
    return jwtUtils.getCleanJwtCookie().toString();
  }
}