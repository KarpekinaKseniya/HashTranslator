package tt.hashtranslator.service;

import static java.util.Objects.isNull;
import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import tt.hashtranslator.domain.entity.IdSequence;

@Service
@AllArgsConstructor
public class IdGeneratorServiceImpl implements IdGeneratorService {

  private static final String ID_NAME = "_id";
  private static final String KEY_NAME = "sequence";

  private final MongoOperations mongoOperations;

  @Override
  public Long generateIdSequence(String name) {
    final IdSequence counter = mongoOperations.findAndModify(query(where(ID_NAME).is(name)),
        new Update().inc(KEY_NAME, 1), options().returnNew(true).upsert(true),
        IdSequence.class);

    return isNull(counter) ? 1 : counter.getSequence();
  }
}
