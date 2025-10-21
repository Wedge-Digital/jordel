package com.bloodbowlclub.auth.domain.value_objects;

import com.bloodbowlclub.lib.domain.EntityID;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class EntityIDTests {

    void assertHasError(EntityID entityID, String error) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<EntityID>> violations = validator.validate(entityID);
        List<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        Assertions.assertThat(messages).contains(error);
    }

    @Test
    void testEntityIDCreationWithBadStringFails() {
        EntityID entityID = new EntityID("1234567890");
        assertHasError(entityID, "Identifiant non valide, doit être un ULID");
    }

    @Test
    void testEntityIDWithEmptyStringFails() {
        // Test creating an EntityID with a valid ID
        EntityID entityID = new EntityID("");
        assertHasError(entityID, "cannot be empty");
    }

    @Test
    void testEntityIDWithNullStringFails() {
        // Test creating an EntityID with a valid ID
        EntityID entityID = new EntityID(null);
        assertHasError(entityID, "cannot be empty");
        assertHasError(entityID, "Identifiant non valide, doit être un ULID");
    }

    @Test
    void testCreateEntityWithCorrectLengtButInvalidUlidFails() {
        String validId = "JLLJLLJLLJLLJLLJLLJLLJLLIO";
        EntityID entityID = new EntityID(null);
        assertHasError(entityID, "Identifiant non valide, doit être un ULID");
    }

}
