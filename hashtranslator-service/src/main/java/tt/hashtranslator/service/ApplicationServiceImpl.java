package tt.hashtranslator.service;

import static java.lang.String.format;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tt.hashtranslator.domain.entity.Application;
import tt.hashtranslator.domain.request.ApplicationRequest;
import tt.hashtranslator.domain.response.ApplicationResponse;
import tt.hashtranslator.exception.EntityNotFoundException;
import tt.hashtranslator.repository.ApplicationRepository;
import tt.hashtranslator.transformer.ApplicationTransformer;

@Service
public class ApplicationServiceImpl implements ApplicationService {

  private final ApplicationRepository repository;
  private final ApplicationTransformer transformer;
  private final DecoderService decoderService;

  public ApplicationServiceImpl(
      final ApplicationRepository repository,
      final ApplicationTransformer transformer,
      final DecoderService decoderService) {
    this.repository = repository;
    this.transformer = transformer;
    this.decoderService = decoderService;
  }

  @Transactional
  @Override
  public String create(final ApplicationRequest request) {
    final Application saved = repository.save(transformer.requestToEntity(request));
    decoderService.decode(saved);
    return saved.getId();
  }

  @Override
  public ApplicationResponse findById(final String id) {
    return transformer.entityToResponse(
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(format("Application with id = %s not found", id))));
  }
}
