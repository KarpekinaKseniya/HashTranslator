package tt.hashtranslator.client;

import org.springframework.http.HttpHeaders;
import tt.hashtranslator.domain.request.LoginRequest;

public interface AuthClient {

    HttpHeaders login(LoginRequest request);
}
