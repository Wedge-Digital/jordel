package com.bloodbowlclub;


import com.bloodbowlclub.lib.services.JsonService;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.lang.StackWalker;

/**
 * Test-only JSON assertions that leverage JUnit's assertEquals so IDEs can show rich diffs.
 */
public final class JsonAssertions {
    private JsonAssertions() {}

    private static final String FIXTURE_BASE = "src/test/_fixtures/resultsets/java/com/bloodbowlclub/";
    private static final StackWalker WALKER = StackWalker.getInstance(
            StackWalker.Option.RETAIN_CLASS_REFERENCE
    );

    private static String getFixture() {
        StackWalker.StackFrame frame = WALKER.walk(stream -> stream.skip(2).findFirst().orElseThrow());

        String fileName   = frame.getFileName();
        String className  = frame.getClassName();
        String methodName = frame.getMethodName();
        return fileName.substring(0,fileName.indexOf(".java"))+ "__" +methodName+".json";
    }

    public static void assertEqualsFixture(Object jsonObject, List<String> fieldToExclude) throws IOException {

        String relativePath = getFixture();
        JsonService jsonService = new JsonService();
        String actual = jsonService.asJsonStringExcludingFields(jsonObject, fieldToExclude);

        String expected="";
        String filename = FIXTURE_BASE + relativePath;
        expected = Files.readString(Path.of(filename));
//        try {
//        } catch (NoSuchFileException exc) {
//            File file = new File(filename);
//            file.createNewFile();
//        }

        String expPretty = JsonService.pretty(expected);
        String actPretty = JsonService.pretty(actual);

        // Use JUnit assertion carrying expected/actual so IntelliJ shows "Click to see difference"
        Assertions.assertEquals(expPretty, actPretty);
    }
}
