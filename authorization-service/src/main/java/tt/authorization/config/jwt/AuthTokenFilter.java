package tt.authorization.config.jwt;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import tt.authorization.service.auth.UserDetailsServiceImpl;

@AllArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

  private final JwtUtils jwtUtils;
  private final UserDetailsServiceImpl userDetailsService;

  @Override
  protected void doFilterInternal(final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain) throws ServletException, IOException {
    try {
      final String jwt = jwtUtils.getJwtFromCookies(request);
      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
        final String username = jwtUtils.getUserNameFromJwtToken(jwt);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (final Exception e) {
      logger.error("Cannot set user authentication: {}", e);
    }

    filterChain.doFilter(request, response);
  }
}
