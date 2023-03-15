package tt.hashtranslator.transformer;

import tt.hashtranslator.domain.entity.Application;
import tt.hashtranslator.domain.request.ApplicationRequest;
import tt.hashtranslator.domain.response.ApplicationResponse;

public interface ApplicationTransformer {

  Application requestToEntity(ApplicationRequest request);

  ApplicationResponse entityToResponse(Application application);
}
