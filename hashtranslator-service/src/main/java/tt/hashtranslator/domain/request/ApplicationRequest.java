package tt.hashtranslator.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRequest {

  @NotNull private List<@NotBlank @Size(min = 32, max = 32) String> hashes;
}
