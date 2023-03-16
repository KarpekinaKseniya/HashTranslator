package tt.hashtranslator.service;

import tt.hashtranslator.domain.request.ApplicationRequest;
import tt.hashtranslator.domain.response.ApplicationResponse;

public interface ApplicationService {

  String create(ApplicationRequest request);

  ApplicationResponse findById(String id);
}
