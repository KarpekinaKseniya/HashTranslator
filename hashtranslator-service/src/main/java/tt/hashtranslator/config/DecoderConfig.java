package tt.hashtranslator.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;

import java.time.Duration;

import static io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType.COUNT_BASED;
import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;

@Configuration
public class DecoderConfig {

  private final String md5DecoderUri;

  private final int readTimeout;

  private final int writeTimeout;

  private final int connectTimeout;

  private final int minNumberOfCalls;

  public DecoderConfig(
      @Value("${decoder.url}") final String md5DecoderUri,
      @Value("${decoder.read.timeout.sec}") final int readTimeout,
      @Value("${decoder.write.timeout.sec}") final int writeTimeout,
      @Value("${decoder.connect.timeout.ms}") final int connectTimeout,
      @Value("${decoder.min.calls}") final int minNumberOfCalls) {
    this.md5DecoderUri = md5DecoderUri;
    this.readTimeout = readTimeout;
    this.writeTimeout = writeTimeout;
    this.connectTimeout = connectTimeout;
    this.minNumberOfCalls = minNumberOfCalls;
  }

  @Bean
  public RestTemplate restTemplateClient() {
    return new RestTemplate();
  }

  @Bean
  public WebClient hashDecoderClient() {
    final HttpClient httpClient =
        HttpClient.create()
            .option(CONNECT_TIMEOUT_MILLIS, connectTimeout)
            .doOnConnected(
                connection ->
                    connection
                        .addHandlerLast(new ReadTimeoutHandler(readTimeout))
                        .addHandlerLast(new WriteTimeoutHandler(writeTimeout)));
    final ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
    return WebClient.builder().baseUrl(md5DecoderUri).clientConnector(connector).build();
  }

  @Bean
  public CircuitBreakerConfigCustomizer externalServiceFooCircuitBreakerConfig() {
    return CircuitBreakerConfigCustomizer.of(
        "decoderService",
        builder ->
            builder
                .slidingWindowSize(10)
                .slidingWindowType(COUNT_BASED)
                .waitDurationInOpenState(Duration.ofSeconds(5))
                .minimumNumberOfCalls(minNumberOfCalls)
                .failureRateThreshold(50.0f));
  }
}
