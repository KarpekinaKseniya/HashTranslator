package tt.hashtranslator.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {
  private List<HashResponse> successHashes;
  private List<HashResponse> failedHashes;
}
