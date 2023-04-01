package tt.hashtranslator.service;

import tt.hashtranslator.domain.request.ApplicationRequest;
import tt.hashtranslator.domain.response.ApplicationResponse;

public interface ApplicationService {

  Long create(ApplicationRequest request);

  ApplicationResponse findById(Long id);
}
