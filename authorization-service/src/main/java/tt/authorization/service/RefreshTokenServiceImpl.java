package tt.authorization.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tt.authorization.config.jwt.JwtUtils;
import tt.authorization.domain.entity.RefreshToken;
import tt.authorization.exception.EntityNotFoundException;
import tt.authorization.exception.TokenRefreshException;
import tt.authorization.repository.RefreshTokenRepository;
import tt.authorization.repository.UserRepository;
import tt.authorization.service.auth.UserDetailsImpl;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.UUID;

import static java.lang.String.format;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

  private static final String NOT_FOUND_ERROR = "%s with %s = %s didn't found.";

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;
  private final JwtUtils jwtUtils;
  private final Long refreshTokenDurationMs;

  public RefreshTokenServiceImpl(
      RefreshTokenRepository refreshTokenRepository,
      UserRepository userRepository,
      JwtUtils jwtUtils,
      @Value("${app.jwtRefreshExpirationMs}") Long refreshTokenDurationMs) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.userRepository = userRepository;
    this.jwtUtils = jwtUtils;
    this.refreshTokenDurationMs = refreshTokenDurationMs;
  }

  @Override
  public String findByToken(final HttpServletRequest request) {
    String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);

    if ((refreshToken != null) && (refreshToken.length() > 0)) {
      return refreshTokenRepository
          .findByToken(refreshToken)
          .map(this::verifyExpiration)
          .map(RefreshToken::getUser)
          .map(
              user -> {
                ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);
                return jwtCookie.toString();
              })
          .orElseThrow(
              () -> new TokenRefreshException(refreshToken, "Refresh token is not in database!"));
    }
    throw new IllegalArgumentException("Refresh Token is empty!");
  }

  @Override
  public String createRefreshToken(final String userEmail) {
    RefreshToken refreshToken = new RefreshToken();

    refreshToken.setUser(
        userRepository
            .findByEmail(userEmail)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(format(NOT_FOUND_ERROR, "User", "id", userEmail))));
    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
    refreshToken.setToken(UUID.randomUUID().toString());

    refreshToken = refreshTokenRepository.save(refreshToken);

    return jwtUtils.generateRefreshJwtCookie(refreshToken.getToken()).toString();
  }

  @Override
  @Transactional
  public String cleanRefreshToken(final Object userDetails) {
    if (!"anonymousUser".equals(userDetails.toString())) {
      Long userId = ((UserDetailsImpl) userDetails).getId();
      deleteByUserId(userId);
    }
    return jwtUtils.getCleanJwtRefreshCookie().toString();
  }

  private RefreshToken verifyExpiration(final RefreshToken token) {
    if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(token);
      throw new TokenRefreshException(
          token.getToken(), "Refresh token was expired. Please make a new login request");
    }

    return token;
  }

  private void deleteByUserId(final Long userId) {
    refreshTokenRepository.deleteByUser(
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> new EntityNotFoundException(format(NOT_FOUND_ERROR, "User", "id", userId))));
  }
}
