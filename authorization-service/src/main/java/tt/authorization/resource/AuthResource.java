package tt.authorization.resource;

import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.ok;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tt.authorization.domain.request.LoginRequest;
import tt.authorization.domain.response.ErrorResponse;
import tt.authorization.service.TokenService;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthResource {

  private static final String LOGOUT_MESSAGE = "You've been sighed out!";
  private static final String REFRESH_TOKEN_MESSAGE = "Token is refreshed successfully!";
  private final TokenService tokenService;

  @Operation(summary = "Login", description = "Endpoint for sign in", responses = {
      @ApiResponse(responseCode = "200", description = "Login Success"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Invalid user credentials", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @PostMapping("/login")
  public ResponseEntity<Void> login(@Valid @RequestBody final LoginRequest request) {
    return ok().header(SET_COOKIE, tokenService.generateAccessToken(request))
        .header(SET_COOKIE, tokenService.generateRefreshToken(request.getEmail())).build();
  }

  @Operation(summary = "Logout", description = "Endpoint for logout", responses = {
      @ApiResponse(responseCode = "200", description = "Logout Success"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @PostMapping("/logout")
  public ResponseEntity<String> logoutUser() {
    return ok().header(SET_COOKIE, tokenService.cleanAccessToken())
        .header(SET_COOKIE, tokenService.cleanRefreshToken()).body(LOGOUT_MESSAGE);
  }

  @Operation(summary = "Refresh token", description = "Endpoint for refreshing token", responses = {
      @ApiResponse(responseCode = "200", description = "Refresh Token Success"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @PostMapping("/token/refresh")
  public ResponseEntity<String> refreshToken(final HttpServletRequest request) {
    return ResponseEntity.ok().header(SET_COOKIE, tokenService.findByToken(request))
        .body(REFRESH_TOKEN_MESSAGE);
  }

  @Operation(summary = "Check user's auth", description = "Endpoint for checking user's authentication", responses = {
      @ApiResponse(responseCode = "200", description = "User is authenticated"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @PostMapping
  public ResponseEntity<Void> checkAuth() {
    return new ResponseEntity<>(OK);
  }
}
