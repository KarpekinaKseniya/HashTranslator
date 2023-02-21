package tt.authorization.service;

import javax.servlet.http.HttpServletRequest;

public interface RefreshTokenService {

  String findByToken(HttpServletRequest request);

  String createRefreshToken(String userEmail);

  String cleanRefreshToken(Object userDetails);
}
