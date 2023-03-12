package tt.authorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import tt.authorization.config.RsaProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaProperties.class)
@ConfigurationPropertiesScan("tt.authorization.config")
public class AuthorizationApplication {
  public static void main(final String[] args) {
    SpringApplication.run(tt.authorization.AuthorizationApplication.class, args);
  }
}
