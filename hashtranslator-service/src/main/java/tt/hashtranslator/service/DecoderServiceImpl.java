package tt.hashtranslator.service;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;
import static reactor.core.publisher.Mono.just;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import tt.hashtranslator.domain.entity.Application;
import tt.hashtranslator.repository.ApplicationRepository;

@Slf4j
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
    final Flux<String> resultHashes = Flux.fromIterable(request).flatMap(this::sendHashesToDecoder);
    final Mono<List<String>> failed = resultHashes.filter(request::contains).collectList();
    final Mono<List<String>> success = resultHashes.filter(hash -> !request.contains(hash))
        .collectList();
    success.publishOn(Schedulers.boundedElastic()).map(successHash -> {
      application.setSuccessResult(successHash);
      return applicationRepository.save(application);
    }).subscribe(
        app -> log.info("Finished processing with Success Hashes = {}", app.getSuccessResult()));
    failed.publishOn(Schedulers.boundedElastic()).map(failedHash -> {
      application.setFailedResult(failedHash);
      return applicationRepository.save(application);
    }).subscribe(
        app -> log.info("Finished processing with Failed Hashes = {}", app.getFailedResult()));
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
