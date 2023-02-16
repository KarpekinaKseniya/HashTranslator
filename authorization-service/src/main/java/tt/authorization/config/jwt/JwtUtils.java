package tt.authorization.config.jwt;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.ResponseCookie;
import tt.authorization.service.auth.UserDetailsImpl;

//TODO if necessary
public class JwtUtils {

    private String jwtSecret;
    private String globalSalt;
    private int jwtExpirationMs;
    private String jwtCookie;

    public String getJwtFromCookies(final HttpServletRequest request) {
        throw new NotImplementedException();
    }

    public ResponseCookie generateJwtCookie(final UserDetailsImpl userPrincipal) {
        throw new NotImplementedException();
    }

    public ResponseCookie getCleanJwtCookie() {
        throw new NotImplementedException();
    }

    public String getUserNameFromJwtToken(final String token) {
        throw new NotImplementedException();
    }

    public boolean validateJwtToken(final String authToken) {
        throw new NotImplementedException();
    }

    public String generateTokenFromEmail(final String email) {
        throw new NotImplementedException();
    }
}
