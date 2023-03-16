package tt.hashtranslator.resource;

import static org.springframework.http.ResponseEntity.ok;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tt.hashtranslator.client.AuthClient;
import tt.hashtranslator.domain.request.LoginRequest;
import tt.hashtranslator.domain.response.ErrorResponse;

@RestController
@RequestMapping("api/v1/auth")
public class AuthResource {

    private final AuthClient authClient;

    public AuthResource(final AuthClient authClient) {
        this.authClient = authClient;
    }

    @Operation(summary = "Login", description = "Endpoint for sign in", responses = {
        @ApiResponse(responseCode = "200", description = "Login Success"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid user credentials", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody final LoginRequest request) {
        return ok().headers(authClient.login(request)).build();
    }
}
