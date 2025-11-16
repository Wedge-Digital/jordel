
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Test-only JSON assertions that leverage JUnit's assertEquals so IDEs can show rich diffs.
 */
public final class JsonAssertions {
    private JsonAssertions() {}

    private static final String FIXTURE_BASE = "src/test/_fixtures/resultsets/java/com/td/telecareproxy/";

    public static void assertEqualsFixture(Object jsonObject, String fixtureRelativePath, boolean excludeIdsAndTimestamps) throws IOException {
        JsonService jsonService = new JsonService();
        String actual = excludeIdsAndTimestamps
                ? jsonService.asJsonStringWithoutIdAndTimestamp(jsonObject)
                : jsonService.asJsonString(jsonObject);

        String expected = Files.readString(Path.of(FIXTURE_BASE + fixtureRelativePath));

        String expPretty = JsonService.pretty(expected);
        String actPretty = JsonService.pretty(actual);

        // Use JUnit assertion carrying expected/actual so IntelliJ shows "Click to see difference"
        Assertions.assertEquals(expPretty, actPretty);
    }

    public static void assertEqualsFixture(Object jsonObject, String fixtureRelativePath, List<String> fieldToExclude) throws IOException {
        JsonService jsonService = new JsonService();
        String actual = jsonService.asJsonStringExcludingFields(jsonObject, fieldToExclude);

        String expected = Files.readString(Path.of(FIXTURE_BASE + fixtureRelativePath));

        String expPretty = JsonService.pretty(expected);
        String actPretty = JsonService.pretty(actual);

        // Use JUnit assertion carrying expected/actual so IntelliJ shows "Click to see difference"
        Assertions.assertEquals(expPretty, actPretty);
    }
}
