package tt.hashtranslator.domain.entity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("applications")
public class Application {

  @Transient
  public static final String SEQUENCE_NAME = "custom_sequence";
  @Id
  private Long id;
  private Status status;
  private List<String> hashes;
  private List<String> successResult;
  private List<String> failedResult;
}
