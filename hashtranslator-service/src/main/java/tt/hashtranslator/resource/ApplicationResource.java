package tt.hashtranslator.resource;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.ResponseEntity.ok;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tt.hashtranslator.domain.request.ApplicationRequest;
import tt.hashtranslator.domain.response.ApplicationResponse;
import tt.hashtranslator.domain.response.ErrorResponse;
import tt.hashtranslator.service.ApplicationService;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationResource {

  private final ApplicationService service;

  public ApplicationResource(final ApplicationService service) {
    this.service = service;
  }

  @Operation(summary = "Send application to decode MD5", description = "Endpoint for sending application to decode MD5", responses = {
      @ApiResponse(responseCode = "202", description = "Application Accepted", content = @Content(schema = @Schema(implementation = Long.class))),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @PostMapping
  public ResponseEntity<String> send(@Valid @RequestBody final ApplicationRequest request) {
    return new ResponseEntity<>(service.create(request), ACCEPTED);
  }

  @Operation(summary = "Get application", description = "Endpoint for getting application by id", responses = {
      @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @GetMapping("/{id}")
  public ResponseEntity<ApplicationResponse> get(@PathVariable final String id) {
    return ok(service.findById(id));
  }
}
