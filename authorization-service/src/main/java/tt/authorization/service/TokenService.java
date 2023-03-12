package tt.authorization.service;

import tt.authorization.domain.request.LoginRequest;

import javax.servlet.http.HttpServletRequest;

public interface TokenService {

    String generateAccessToken(LoginRequest request);

    String generateRefreshToken(String email);

    String findByToken(final HttpServletRequest request);

    String cleanAccessToken();

    String cleanRefreshToken();
}
