package tt.hashtranslator.domain.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {

  private List<HashResponse> successHashes;
  private List<HashResponse> failedHashes;
}
