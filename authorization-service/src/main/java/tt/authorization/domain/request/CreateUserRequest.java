package tt.authorization.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tt.authorization.domain.entity.ERole;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

  @Size(max = 25, message = "Firstname size must be between 0 and 25")
  private String firstname;

  @Size(max = 25, message = "Lastname size must be between 0 and 25")
  private String lastname;

  private Set<ERole> role;

  @NotBlank(message = "Email must not be blank")
  @Email
  private String email;

  @NotNull(message = "Password must not be null")
  @Size(max = 60, message = "Password size must be between 0 and 60")
  @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$",
      message =
          "Password must contain at least one digit [0-9].\n"
              + "Password must contain at least one lowercase Latin character [a-z].\n"
              + "Password must contain at least one uppercase Latin character [A-Z].\n"
              + "Password must contain at least one special character like ! @ # & ( ).\n"
              + "Password must contain a length of at least 8 characters and a maximum of 20 characters.")
  private String password;
}
