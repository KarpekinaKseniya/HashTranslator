package tt.authorization.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tt.authorization.domain.request.LoginRequest;
import tt.authorization.service.RefreshTokenService;
import tt.authorization.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("api/v1/auth")
public class AuthResource {

  private static final String LOGOUT_MESSAGE = "You've been sighed out!";
  private static final String REFRESH_TOKEN_MESSAGE = "Token is refreshed successfully!";

  private final UserService userService;
  private final RefreshTokenService refreshTokenService;

  public AuthResource(
      final UserService userService, final RefreshTokenService refreshTokenService) {
    this.userService = userService;
    this.refreshTokenService = refreshTokenService;
  }

  @PostMapping("/login")
  public ResponseEntity<Void> auth(@Valid @RequestBody final LoginRequest request) {
    return ok().header(SET_COOKIE, userService.login(request))
        .header(SET_COOKIE, refreshTokenService.createRefreshToken(request.getEmail()))
        .build();
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logoutUser() {
    final Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return ok().header(SET_COOKIE, userService.logout())
        .header(SET_COOKIE, refreshTokenService.cleanRefreshToken(principle))
        .body(LOGOUT_MESSAGE);
  }

  @PostMapping("/refreshtoken")
  public ResponseEntity<String> refreshToken(final HttpServletRequest request) {
    return ResponseEntity.ok()
        .header(SET_COOKIE, refreshTokenService.findByToken(request))
        .body(REFRESH_TOKEN_MESSAGE);
  }
}
