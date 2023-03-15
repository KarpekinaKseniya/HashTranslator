package tt.hashtranslator.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tt.hashtranslator.domain.request.ApplicationRequest;
import tt.hashtranslator.domain.response.ApplicationResponse;
import tt.hashtranslator.service.ApplicationService;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationResource {

  private final ApplicationService service;

  public ApplicationResource(final ApplicationService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<String> send(@Valid @RequestBody final ApplicationRequest request) {
    return new ResponseEntity<>(service.create(request), ACCEPTED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApplicationResponse> get(@PathVariable final String id) {
    return ok(service.findById(id));
  }
}
