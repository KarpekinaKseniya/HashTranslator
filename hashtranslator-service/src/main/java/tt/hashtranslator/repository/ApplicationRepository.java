package tt.hashtranslator.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tt.hashtranslator.domain.entity.Application;

@Repository
public interface ApplicationRepository extends MongoRepository<Application, Long> {}
