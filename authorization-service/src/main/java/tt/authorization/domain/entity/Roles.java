package tt.authorization.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "roles",
    uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
public class Roles {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Enumerated(STRING)
  @Column(length = 15)
  private ERole name;
}
