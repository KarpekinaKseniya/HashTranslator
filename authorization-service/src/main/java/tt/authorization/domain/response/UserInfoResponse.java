package tt.authorization.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tt.authorization.domain.entity.Roles;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
  private String firstname;
  private String lastname;
  private Set<Roles> roles;
  private String email;
}
