package tt.hashtranslator.listener;

import static java.util.Objects.isNull;
import static tt.hashtranslator.domain.entity.Application.SEQUENCE_NAME;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import tt.hashtranslator.domain.entity.Application;
import tt.hashtranslator.service.IdGeneratorService;

@Component
@AllArgsConstructor
public class ApplicationListener extends AbstractMongoEventListener<Application> {

  private final IdGeneratorService generatorService;

  @Override
  public void onBeforeConvert(final BeforeConvertEvent<Application> event) {
    final Application application = event.getSource();
    if (isNull(application.getId())) {
      application.setId(generatorService.generateIdSequence(SEQUENCE_NAME));
    }
  }
}
