package tt.hashtranslator.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document("id_sequences")
public class IdSequence {

  @Id
  private String id;
  private Long sequence;
}
