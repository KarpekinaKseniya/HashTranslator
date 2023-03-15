package tt.authorization.serialisation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Supplier;

import static com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

abstract class JsonTestBase<T> {

  private static final String FILE_PATH = "src/test/resources/serialisation/";

  protected Supplier<T> expected;
  protected String fileName;
  protected Class<T> expectedType;

  private ObjectMapper mapper;

  @BeforeEach
  void setup() {
    mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.enable(WRITE_BIGDECIMAL_AS_PLAIN);
  }

  @Test
  void shouldSerialize() throws IOException {
    final T dto = expected.get();

    final String actualJson =
        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto).replaceAll("\\r", "");

    assertJson(actualJson, fileName);
  }

  @Test
  void shouldDeserialize() throws IOException {
    final T expected = this.expected.get();

    final String expectedJson = readFileAsString(fileName);

    final T actual = mapper.readValue(expectedJson, expectedType);

    assertThat(actual, is(expected));
  }

  private void assertJson(final String actualJson, final String expectedJsonFile)
      throws IOException {
    final String expectedJson = readFileAsString(expectedJsonFile).replaceAll("\\r", "");

    assertThat("JSON strings did not match", actualJson, is(expectedJson));
  }

  private String readFileAsString(String file) throws IOException {
    return new String(Files.readAllBytes(Paths.get(FILE_PATH + file)));
  }
}
