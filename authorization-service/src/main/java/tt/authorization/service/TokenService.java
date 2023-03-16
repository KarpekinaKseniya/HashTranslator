package tt.authorization.service;

import javax.servlet.http.HttpServletRequest;
import tt.authorization.domain.request.LoginRequest;

public interface TokenService {

    String generateAccessToken(LoginRequest request);

    String generateRefreshToken(String email);

    String findByToken(final HttpServletRequest request);

    String cleanAccessToken();

    String cleanRefreshToken();
}
