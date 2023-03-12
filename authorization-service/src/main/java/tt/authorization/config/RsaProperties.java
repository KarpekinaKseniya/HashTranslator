package tt.authorization.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "rsa")
public class RsaProperties {

    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
}
