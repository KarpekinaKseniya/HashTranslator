package tt.authorization.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tt.authorization.domain.request.CreateUserRequest;
import tt.authorization.domain.response.ErrorResponse;
import tt.authorization.domain.response.UserInfoResponse;
import tt.authorization.service.UserService;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/account")
public class AccountResource {
  private final UserService userService;

  public AccountResource(final UserService userService) {
    this.userService = userService;
  }

  @Operation(
      summary = "Get User info by id",
      description = "Endpoint for getting user info by id",
      responses = {
        @ApiResponse(responseCode = "200", description = "Ok"),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #id")
  public ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable final Long id) {
    return ok(userService.getUserInfo(id));
  }

  @Operation(
      summary = "Delete user by ID",
      description = "Endpoint for deleting user by ID",
      responses = {
        @ApiResponse(responseCode = "204", description = "Success"),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable final Long id) {
    userService.delete(id);
    return new ResponseEntity<>(NO_CONTENT);
  }

  @Operation(
      summary = "Add new user",
      description = "Endpoint for added user",
      responses = {
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  @PostMapping
  public ResponseEntity<Long> createUser(@Valid @RequestBody final CreateUserRequest request) {
    return new ResponseEntity<>(userService.createUser(request), CREATED);
  }
}
