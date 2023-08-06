package tt.hashtranslator.transformer;

import org.springframework.stereotype.Component;
import tt.hashtranslator.domain.entity.Application;
import tt.hashtranslator.domain.request.ApplicationRequest;
import tt.hashtranslator.domain.response.ApplicationResponse;
import tt.hashtranslator.domain.response.HashResponse;

import java.util.ArrayList;
import java.util.List;

import static tt.hashtranslator.domain.entity.Status.ACCEPTED;

@Component
public class ApplicationTransformerImpl implements ApplicationTransformer {

  @Override
  public Application requestToEntity(final ApplicationRequest request) {
    if (request == null) {
      return null;
    }
    final Application application = new Application();
    final List<String> hashes = request.getHashes();
    if (!hashes.isEmpty()) {
      application.setHashes(hashes);
    }
    application.setStatus(ACCEPTED);
    return application;
  }

  @Override
  public ApplicationResponse entityToResponse(final Application application) {
    if (application == null) {
      return null;
    }
    final ApplicationResponse response = new ApplicationResponse();
    final List<String> successResult = application.getSuccessResult();
    if (!successResult.isEmpty()) {
      final List<HashResponse> successResponse = new ArrayList<>();
      for (final String success : successResult) {
        successResponse.add(new HashResponse(success));
      }
      response.setSuccessHashes(successResponse);
    }
    final List<String> failedResult = application.getFailedResult();
    if (!failedResult.isEmpty()) {
      final List<HashResponse> failedResponse = new ArrayList<>();
      for (final String failed : failedResult) {
        failedResponse.add(new HashResponse(failed));
      }
      response.setFailedHashes(failedResponse);
    }
    response.setStatus(application.getStatus());
    return response;
  }
}
