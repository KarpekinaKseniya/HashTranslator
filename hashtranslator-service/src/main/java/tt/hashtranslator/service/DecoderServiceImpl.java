package tt.hashtranslator.service;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;
import static reactor.core.publisher.Mono.just;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tt.hashtranslator.domain.entity.Application;
import tt.hashtranslator.repository.ApplicationRepository;

@Service
public class DecoderServiceImpl implements DecoderService {

  private static final String EMPTY_STRING = "";
  private static final String HASH_NAME = "hash";
  private static final String HASH_TYPE_NAME = "type";

  private final String hashType;
  private final String hasPath;
  private final ApplicationRepository applicationRepository;
  private final WebClient hashDecoderClient;

  public DecoderServiceImpl(
      @Value("${hash.type}") final String hashType,
      @Value("${hash.path}") final String hasPath,
      final ApplicationRepository applicationRepository,
      final WebClient hashDecoderClient) {
    this.hashType = hashType;
    this.hasPath = hasPath;
    this.applicationRepository = applicationRepository;
    this.hashDecoderClient = hashDecoderClient;
  }

  @Async
  @Override
  public void decode(final Application application) {
    final List<String> request = application.getHashes().stream().map(String::toLowerCase)
        .collect(toList());
    final List<String> resultHashes =
        Flux.fromIterable(request)
            .flatMap(this::sendHashesToDecoder)
            .collect(toList())
            .share()
            .block();
    final List<String> failed =
        nonNull(resultHashes)
            ? failedResult(resultHashes, request)
            : new ArrayList<>(application.getHashes());
    final List<String> success =
        nonNull(resultHashes)
            ? successResult(resultHashes, request)
            : new ArrayList<>(application.getHashes());
    application.setFailedResult(failed);
    application.setSuccessResult(success);
    applicationRepository.save(application);
  }

  private List<String> failedResult(final List<String> resultHashes, final List<String> request) {
    return resultHashes.stream().filter(request::contains).collect(toList());
  }

  private List<String> successResult(final List<String> resultHashes, final List<String> request) {
    return resultHashes.stream().filter(hash -> !request.contains(hash)).collect(toList());
  }

  private Mono<String> sendHashesToDecoder(final String hash) {
    return hashDecoderClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(hasPath)
                    .queryParam(HASH_NAME, hash)
                    .queryParam(HASH_TYPE_NAME, hashType)
                    .build())
        .exchangeToMono(
            response ->
                response.statusCode().equals(OK)
                    ? response.bodyToMono(String.class).defaultIfEmpty(hash)
                    : just(EMPTY_STRING));
  }
}
