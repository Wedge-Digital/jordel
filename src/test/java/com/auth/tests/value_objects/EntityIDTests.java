package com.auth.tests.value_objects;

import com.auth.domain.EntityID;
import com.auth.services.Result;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ulid4j.Ulid;

import static com.auth.services.errors.ErrorType.FORMAT_VALIDATION_ERROR;

public class EntityIDTests {

    @Test
    void testEntityIDCreationWithBadStringFails() {
        // Test creating an EntityID with a valid ID
        String validId = "1234567890";
        Result<EntityID> IDCreationResult = EntityID.fromString(validId);
        Assertions.assertThat(IDCreationResult.isSuccess()).isFalse();
        Assertions.assertThat(IDCreationResult.getError().getType()).isEqualTo(FORMAT_VALIDATION_ERROR);
        Assertions.assertThat(IDCreationResult.getError().getMessage()).isEqualTo("EntityID must be alphanumeric and 26 characters");
    }

    @Test
    void testEntityIDWithEmptyStringFails() {
        // Test creating an EntityID with a valid ID
        String validId = "";
        Result<EntityID> IDCreationResult = EntityID.fromString(validId);
        Assertions.assertThat(IDCreationResult.isSuccess()).isFalse();
        Assertions.assertThat(IDCreationResult.getError().getType()).isEqualTo(FORMAT_VALIDATION_ERROR);
        Assertions.assertThat(IDCreationResult.getError().getMessage()).isEqualTo("EntityID cannot be empty");
    }

    @Test
    void testEntityIDWithNullStringFails() {
        // Test creating an EntityID with a valid ID
        String validId = null;
        Result<EntityID> IDCreationResult = EntityID.fromString(validId);
        Assertions.assertThat(IDCreationResult.isSuccess()).isFalse();
        Assertions.assertThat(IDCreationResult.getError().getType()).isEqualTo(FORMAT_VALIDATION_ERROR);
        Assertions.assertThat(IDCreationResult.getError().getMessage()).isEqualTo("EntityID cannot be null");
    }

    @Test
    void testCreateEntityWithCorrectLengtButInvalidUlidFails() {
        String validId = "JLLJLLJLLJLLJLLJLLJLLJLLIO";
        Result<EntityID> IDCreationResult = EntityID.fromString(validId);
        Assertions.assertThat(IDCreationResult.isSuccess()).isFalse();
        Assertions.assertThat(IDCreationResult.getError().getType()).isEqualTo(FORMAT_VALIDATION_ERROR);
        Assertions.assertThat(IDCreationResult.getError().getMessage()).isEqualTo("EntityID must be a valid ULID");
    }

    @Test
    void testCreateEntityIDwithCorrectULIDSucceed() {
        Ulid ulidGenerator = new Ulid();
        String ulid = ulidGenerator.next();
        String validId = ulid.toString();
        Result<EntityID> IDCreationResult = EntityID.fromString(validId);
        Assertions.assertThat(IDCreationResult.isSuccess()).isTrue();
    }

}
