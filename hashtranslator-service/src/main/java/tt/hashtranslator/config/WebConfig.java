package tt.hashtranslator.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tt.hashtranslator.interceptor.AuthInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private static final List<String> ADD_PATH = List.of("/api/v1/applications/**");
  private static final List<String> EXCLUDE_PATH = List.of("/swagger-ui-custom.html",
      "/swagger-ui.html",
      "/swagger-ui/**",
      "/v3/api-docs/**", "/webjars/**", "/swagger-ui/index.html", "/api-docs/**",
      "api/v1/auth/login");

  private final AuthInterceptor authInterceptor;

  public WebConfig(final AuthInterceptor authInterceptor) {
    this.authInterceptor = authInterceptor;
  }

  @Override
  public void addInterceptors(final InterceptorRegistry registry) {
    registry.addInterceptor(authInterceptor)
        .addPathPatterns(ADD_PATH)
        .excludePathPatterns(EXCLUDE_PATH);
  }
}
