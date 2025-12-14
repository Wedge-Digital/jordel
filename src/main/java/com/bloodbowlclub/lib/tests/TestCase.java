package com.bloodbowlclub.lib.tests;

import com.bloodbowlclub.lib.services.DateService;
import com.bloodbowlclub.lib.services.JsonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.POJONode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Setter
@Slf4j
public class TestCase {
    private static Path baseDir;
    private static String fixtureDirCache; // cache de "test/_fixtures"
    public DateService dateService = new FakeDateService("2025-11-03T16:59:05");

    // Jackson configuré pour sorties stables
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .enable(SerializationFeature.INDENT_OUTPUT);

    private static final DateTimeFormatter MYSQL_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MYSQL_DATETIME = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd HH:mm:ss");

    protected MessageSource messageSource = messageSource();

    private ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("lang/messages", "lang/error", "ValidationMessages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }

    public static Path getBaseDir() {
        if (baseDir == null) {
            baseDir = Paths.get("").toAbsolutePath().normalize();
        }
        return baseDir;
    }

    // --- Répertoires divers ---
    public static Path getTmpDir() {
        return getBaseDir().resolve(".donotcommit_tmp");
    }

    public static Path getTmpDiffCmdFilename() {
        return getTmpDir().resolve(".donotcommit_tmp_diff_cmd");
    }

    public static String getTestFixturesDir() {
        if (fixtureDirCache != null) {
            return fixtureDirCache;
        }
        // On part du chemin de la classe de test appelante
        Path callingPath = getCallingFilePath(getCallingTestClass());
        String p = callingPath.toString().replace('\\', '/');
        int pos = p.indexOf("/test");
        if (pos <= 0) {
            throw new IllegalStateException("Unable to determine root test path from " + p);
        }
        String root = p.substring(0, pos);
        Path fixtures = Paths.get(root, "test", "_fixtures");
        if (!Files.exists(fixtures)) {
            fixtures = Paths.get(root, "test", "_fixtures");
        }
        fixtureDirCache = fixtures.toString();
        return fixtureDirCache;
    }

    public static Path getResultsetsDir() {
        return Paths.get(getTestFixturesDir(), "resultsets");
    }

    public static Path getDatasetsDir(String filename) {
        Path base = Paths.get(getTestFixturesDir(), "datasets");
        return filename == null ? base : base.resolve(filename);
    }

    // --- Lecture/écriture datasets ---
    public static Object getDataset(String relativePath) {
        Path datasetPath = getDatasetsDir(relativePath);
        String name = datasetPath.getFileName().toString();
        int dot = name.lastIndexOf('.');
        String ext = (dot >= 0 ? name.substring(dot) : "");
        if (".json".equalsIgnoreCase(ext)) {
            try {
                return MAPPER.readTree(Files.readString(datasetPath));
            } catch (IOException e) {
                log.error("Unable to read dataset " + datasetPath, e);
                return null;
            }
        }

        try {
            return Files.readString(datasetPath);
        } catch (IOException e) {
            log.error("Unable to read dataset " + datasetPath, e);
            return null;
        }
    }

    public static void saveAsDataset(String relativeFilename, Object dataset) throws IOException {
        Path fixtureFilename = getDatasetsDir(relativeFilename);
        saveAsFixture(fixtureFilename, dataset);
    }

    private static void saveAsFixture(Path fixtureFilename, Object dataset) {
        ensureFileCanBeCreated(fixtureFilename);
        String name = fixtureFilename.getFileName().toString().toLowerCase(Locale.ROOT);
        if (name.endsWith(".json")) {
            String json = convertDatasetToJson(dataset, null);
            if (Files.exists(fixtureFilename)) {
                try {
                    String actual = Files.readString(fixtureFilename);
                    if (actual.equals(json)) {
                        return; // pas de changement
                    }
                } catch (IOException e) {
                    log.error("Unable to read file " + fixtureFilename, e);
                }

            }
            try {
                Files.writeString(fixtureFilename, json, StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                log.error("Unable to write file " + fixtureFilename, e);
            }
        } else {
            byte[] bytes = toBytes(dataset);
            if (Files.exists(fixtureFilename)) {
                try {

                    byte[] actual = Files.readAllBytes(fixtureFilename);
                    if (Arrays.equals(actual, bytes)) {
                        return;
                    }
                } catch (Exception e) {
                    log.error("Unable to read file " + fixtureFilename, e);
                }
            }

            try {
                Files.write(fixtureFilename, bytes, StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            } catch (Exception e) {
                log.error("Unable to write file " + fixtureFilename, e);
            }
        }
    }

    private static byte[] toBytes(Object dataset) {
        if (dataset == null) {
            return new byte[0];
        }
        if (dataset instanceof byte[]) {
            return (byte[]) dataset;
        }
        if (dataset instanceof String) {
            return ((String) dataset).getBytes(StandardCharsets.UTF_8);
        }
        try {
            return convertDatasetToJson(dataset, null).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            return dataset.toString().getBytes(StandardCharsets.UTF_8);
        }
    }

    private static String convertDatasetToJson(Object dataset, List<String> fieldToExclude) {
        JsonService jsonService = new JsonService();
        return JsonService.pretty(jsonService.asJsonStringExcludingFields(dataset, fieldToExclude));
    }

    // --- Sandbox ---
    public Path getSandboxDir() {
        return getSandboxDir(null);
    }

    public Path getSandboxDir(String filename) {
        Path base = getRootSandboxDir().resolve(getClass().getSimpleName());
        if (filename == null) {
            return base;
        }
        Path full = base.resolve(filename);
        ensureFileCanBeCreated(full);
        return full;
    }

    private static void cleanSandboxStatic(Class<?> cls) throws IOException {
        Path dir = getRootSandboxDir().resolve(cls.getSimpleName());
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        emptyDir(dir);
    }

    private static void emptySandboxStatic() throws IOException {
        Path dir = getRootSandboxDir();
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        emptyDir(dir);
    }

    public static void cleanSandbox() throws IOException {
        cleanSandboxStatic(getCallingTestClass());
    }

    public static void emptySandbox() throws IOException {
        emptySandboxStatic();
    }

    private static Path getRootSandboxDir() {
        return Paths.get(getTestFixturesDir(), "sandbox");
    }

    private static void emptyDir(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            return;
        }
        try (var s = Files.walk(dir)) {
            s.sorted(Comparator.reverseOrder())
                    .filter(p -> !p.equals(dir))
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException ignored) {
                        }
                    });
        }
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
    }

    // --- Assertions utilitaires ---
    public static void assertFileExists(Path filename) {
        if (!Files.exists(filename)) {
            throw new AssertionError(filename + " not found");
        }
    }

    public static void assertFileNotExists(Path filename) {
        if (Files.exists(filename)) {
            throw new AssertionError(filename + " found");
        }
    }

    /**
     * Vérifie qu'un log attendu est présent dans la liste de messages.
     */
    public static void assertLog(Collection<String> capturedMessages, String expected) {
        boolean ok = capturedMessages.stream().anyMatch(expected::equals);
        if (!ok) {
            StringBuilder sb = new StringBuilder();
            sb.append("\nExpected log:\n'").append(expected).append("'\ngot instead:\n");
            for (String m : capturedMessages) {
                sb.append("- '").append(m).append("'\n");
            }
            throw new AssertionError(sb.toString());
        }
    }

    // --- Assertion resultset (golden file) ---
    public void assertEqualsResultset(Object actual) {
        assertEqualsResultset(actual, TestCaseOptions.builder().build());
    }

    public void assertEqualsResultset(Object actual, TestCaseOptions options) {
        Class<?> testClass = getClass();
        Path callingPath = getCallingFilePath(testClass);
        String methodName = getCallingMethodName();
        if (options.getResultSetNumber() != 1) {
            methodName += "_" + options.getResultSetNumber();
        }
        String subDir = getTestSubdir(callingPath);
        String fileNameNoExt = stripExtension(callingPath.getFileName().toString());
        Path rsFile = getResultsetsDir().resolve(
                Paths.get(subDir, fileNameNoExt, methodName + ".json"));
        ensureFileCanBeCreated(rsFile);

        String actualJson = convertDatasetToJson(actual, options.getFieldToExclude());

        if (!Files.exists(rsFile)) {
            boolean autosave = Boolean.parseBoolean(
                    Optional.ofNullable(System.getenv("TEST_RESULTSET_AUTOSAVE"))
                            .orElse("false"));
            String data = autosave ? actualJson : "{}\n";
            try {
                Files.writeString(rsFile, data, StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                log.error("Unable to write file " + rsFile, e);
                return;
            }
        }
        String expected;
        try {
            expected = Files.readString(rsFile);
        } catch (IOException e) {
            log.error("Unable to read file " + rsFile, e);
            return;
        }

        if (!Objects.equals(expected, actualJson)) {
            Path tmpDir = getTmpDir();
            if (!Files.exists(tmpDir)) {
                try {
                    Files.createDirectories(tmpDir);
                } catch (IOException e) {
                    log.error("Unable to create tmp dir " + tmpDir, e);
                    return;
                }
            }
            String tmpName = fileNameNoExt + "-" + methodName + "_ACTUAL.json";
            Path tmpActual = tmpDir.resolve(tmpName);
            ensureFileCanBeCreated(tmpActual);

            try {
                Files.writeString(tmpActual, actualJson, StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                log.error("Unable to write tmp file " + tmpActual, e);
                return;
            }

            String defaultDiff = Optional.ofNullable(System.getenv("DEFAULT_JUNIT_DIFF_TOOL"))
                    .orElse("idea diff __expected__ __actual__");
            String cmd = defaultDiff.replace("__expected__", rsFile.toString())
                    .replace("__actual__", tmpActual.toString());
            try {
                new ProcessBuilder(shellCommand(cmd)).inheritIO().start();
            } catch (IOException ignored) {
                log.error("Unable to run diff command: " + cmd);
                return;
            }

            System.err.println("\n=== TEST " + methodName + " has failed !!");
            System.err.println("file: " + relativizeToBase(callingPath));
            // Diff simple interne
            printSimpleDiff(expected, actualJson);
            System.err.println("=== TEST " + methodName + "\n");

            throw new AssertionError("Resultset differs. See diff above. Expected file: " + rsFile);
        }
    }

    // --- Outils internes ---
    private static void ensureFileCanBeCreated(Path filename) {
        Path parent = filename.toAbsolutePath().getParent();
        if (parent != null && !Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                log.error("Unable to create directory " + parent, e);
            }
        }
    }

    private static String[] shellCommand(String cmd) {
        if (isWindows()) {
            return new String[]{"cmd", "/c", cmd};
        } else {
            return new String[]{"bash", "-lc", cmd};
        }
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
    }

    private static String stripExtension(String name) {
        int i = name.lastIndexOf('.');
        return (i > 0 ? name.substring(0, i) : name);
    }

    private static String relativizeToBase(Path path) {
        try {
            return getBaseDir().relativize(path).toString();
        } catch (Exception e) {
            return path.toString();
        }
    }

    private static String getTestSubdir(Path callingFile) {
        String p = callingFile.toString().replace('\\', '/');
        String base = getBaseDir().toString().replace('\\', '/');
        if (p.startsWith(base)) {
            p = p.substring(base.length());
        }
        String[] parts = p.startsWith("/") ? p.substring(1).split("/") : p.split("/");
        List<String> list = new ArrayList<>(Arrays.asList(parts));
        while (!list.isEmpty()) {
            String head = list.remove(0);
            if (head.equals("test")) {
                break;
            }
        }
        return String.join("/", list.subList(0, list.size() - 1)); // sans le nom de fichier
    }

    private static Class<?> getCallingTestClass() {
        // La classe concrète qui étend TestCaseMixin
        try {
            for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                String cls = ste.getClassName();
                try {
                    Class<?> c = Class.forName(cls);
                    if (TestCase.class.isAssignableFrom(c) && c != TestCase.class) {
                        return c;
                    }
                } catch (ClassNotFoundException ignored) {
                }
            }
        } catch (Exception ignored) {
        }
        return TestCase.class; // fallback
    }

    private static Path getCallingFilePath(Class<?> c) {
        // Fichier source estimé par nom de classe
        String resource = "/" + c.getName().replace('.', '/') + ".class";
        try {
            var url = c.getResource(resource);
            if (url != null) {
                // On prend la racine projet quand possible
                return getBaseDir().resolve("src").resolve("test").resolve("java")
                        .resolve(c.getName().replace('.', '/')).normalize().toAbsolutePath();
            }
        } catch (Exception ignored) {
        }
        return getBaseDir();
    }

    private static String getCallingMethodName() {
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            try {
                Class<?> c = Class.forName(ste.getClassName());
                if (TestCase.class.isAssignableFrom(c) && c != TestCase.class) {
                    return ste.getMethodName();
                }
            } catch (ClassNotFoundException ignored) {
            }
        }
        // fallback: premier test method probable
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            if (ste.getMethodName().startsWith("test") || ste.getMethodName().contains("(")) {
                return ste.getMethodName();
            }
        }
        return "unknown_test_method";
    }

    private static void printSimpleDiff(String expected, String actual) {
        // Diff très simplifié (ligne à ligne)
        String[] e = expected.split("\r?\n");
        String[] a = actual.split("\r?\n");
        int n = Math.max(e.length, a.length);
        System.err.println("--- expected");
        System.err.println("+++ actual");
        for (int i = 0; i < n; i++) {
            String es = i < e.length ? e[i] : "";
            String as = i < a.length ? a[i] : "";
            if (!Objects.equals(es, as)) {
                System.err.printf("- %s%n", es);
                System.err.printf("+ %s%n", as);
            }
        }
    }

    // --- Conversion de données (équivalent _convert_data) ---
    @SuppressWarnings("unchecked")
    private static Object convertData(Object data) {
        if (data == null) {
            return null;
        }

        if (data instanceof BigDecimal bd) {
            // Comportement Python: Decimal -> float
            return bd.doubleValue();
        }

        if (data instanceof Date date) {
            // Choix: Date -> datetime MySQL
            LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()),
                    ZoneId.systemDefault());
            return MYSQL_DATETIME.format(ldt);
        }

        if (data instanceof LocalDate ld) {
            return MYSQL_DATE.format(ld);
        }

        if (data instanceof LocalDateTime ldt) {
            return MYSQL_DATETIME.format(ldt);
        }

        if (data instanceof Map<?, ?> map) {
            Map<Object, Object> out = new LinkedHashMap<>();
            map.forEach((k, v) -> out.put(k, convertData(v)));
            return out;
        }

        if (data instanceof Collection<?> coll) {
            List<Object> out = new ArrayList<>(coll.size());
            for (Object o : coll) {
                out.add(convertData(o));
            }
            return out;
        }

        if (data.getClass().isArray()) {
            int len = java.lang.reflect.Array.getLength(data);
            List<Object> out = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                out.add(convertData(java.lang.reflect.Array.get(data, i)));
            }
            return out;
        }

        if (data instanceof byte[] bytes) {
            return new String(bytes, StandardCharsets.UTF_8);
        }

        // Si c'est un POJO, on laisse Jackson gérer (via POJONode pour ne pas perdre le type)
        return new POJONode(data);
    }

}
