package com.bloodbowlclub.lib.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class JsonService {
    private static final String fixturePath = "src/test/_fixtures/resultsets/java/com/bloodbowlclub/";

    public String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String asPrettyJsonString(Object value) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serialize to JSON while excluding fields named "id" and "timestampedAt" (case variations included).
     * This removal is applied recursively on nested objects and arrays.
     */
    public String asJsonStringWithoutIdAndTimestamp(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            JsonNode root = mapper.valueToTree(obj);
            removeMetaFieldsRecursively(root, List.of("id", "timestampedAt", "timeStampedAt"));
            return mapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String asJsonStringExcludingFields(final Object obj, List<String> fieldsToExclude) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            JsonNode root = mapper.valueToTree(obj);
            removeMetaFieldsRecursively(root, fieldsToExclude);
            return mapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void removeMetaFieldsRecursively(JsonNode node, List<String> fieldsToExclude) {
        if (node == null) return;
        if (fieldsToExclude == null || fieldsToExclude.isEmpty()) return;

        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            // Remove both spellings to cover DomainEvent's timeStampedAt and a potential timestampedAt
            fieldsToExclude.forEach(field -> obj.remove(field));

            // Iterate remaining fields and recurse
            Iterator<String> fieldNames = obj.fieldNames();
            while (fieldNames.hasNext()) {
                String fname = fieldNames.next();
                JsonNode child = obj.get(fname);
                removeMetaFieldsRecursively(child, fieldsToExclude);
            }
        } else if (node.isArray()) {
            ArrayNode arr = (ArrayNode) node;
            for (JsonNode child : arr) {
                removeMetaFieldsRecursively(child, fieldsToExclude);
            }
        }
    }

    public static String pretty(String json) {
        if (json == null) return null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object tree = mapper.readValue(json, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tree);
        } catch (Exception e) {
            // Si la chaîne n’est pas un JSON valide, on la renvoie telle quelle
            return json;
        }
    }

    public void assertEqualsFixture(Object json, String fixture, boolean excludeIdsAndTimestamps) throws IOException {
        String jsonized = this.asJsonString(json);
        if (excludeIdsAndTimestamps) {
            jsonized = this.asJsonStringWithoutIdAndTimestamp(json);
        }
        String expected = Files.readString(Path.of(JsonService.fixturePath + fixture));
        String expPretty = pretty(expected);
        String actPretty = pretty(jsonized);
        if (!expPretty.equals(actPretty)) {
            String message = "JSON does not match fixture. Expected vs Actual:\nEXPECTED:\n" + expPretty + "\nACTUAL:\n" + actPretty;
            throw new AssertionError(message);
        }
    }

    public Map<String, Object> asMap(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}