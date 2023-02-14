package tt.authorization.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tt.authorization.domain.request.LoginRequest;
import tt.authorization.service.TokenService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("api/v1/auth")
public class AuthResource {

    private static final String LOGOUT_MESSAGE = "You've been sighed out!";
    private static final String REFRESH_TOKEN_MESSAGE = "Token is refreshed successfully!";
    private final TokenService tokenService;

    public AuthResource(final TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> auth(@Valid @RequestBody final LoginRequest request) {
        return ok().header(SET_COOKIE, tokenService.generateAccessToken(request))
                .header(SET_COOKIE, tokenService.generateRefreshToken(request.getEmail())).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser() {
        return ok().header(SET_COOKIE, tokenService.cleanAccessToken())
                .header(SET_COOKIE, tokenService.cleanRefreshToken()).body(LOGOUT_MESSAGE);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<String> refreshToken(final HttpServletRequest request) {
        return ResponseEntity.ok().header(SET_COOKIE, tokenService.findByToken(request)).body(REFRESH_TOKEN_MESSAGE);
    }
}
