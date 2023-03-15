package tt.hashtranslator.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "tt.hashtranslator.repository")
public class MongoDBConfig extends AbstractMongoClientConfiguration {

  private final String databaseName;

  private final String connectionUri;

  public MongoDBConfig(
      @Value("${spring.data.mongodb.database}") final String databaseName,
      @Value("${spring.data.mongodb.uri}") final String connectionUri) {
    this.databaseName = databaseName;
    this.connectionUri = connectionUri;
  }

  @Override
  protected String getDatabaseName() {
    return databaseName;
  }

  @Override
  public MongoClient mongoClient() {
    final ConnectionString connectionString = new ConnectionString(connectionUri);
    final MongoClientSettings mongoClientSettings =
        MongoClientSettings.builder().applyConnectionString(connectionString).build();
    return MongoClients.create(mongoClientSettings);
  }
}
