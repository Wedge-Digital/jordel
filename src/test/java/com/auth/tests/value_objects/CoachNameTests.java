package com.auth.tests.value_objects;

import com.auth.domain.CoachName;
import com.auth.services.Result;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ulid4j.Ulid;

import static com.auth.services.errors.ErrorType.FORMAT_VALIDATION_ERROR;

public class CoachNameTests {

    @Test
    void testCoachNameCreationWithStringTooLongFails() {
        String validId = "JLLJLLJLLJLLJLLJLLJLLJLLIOJLLJLLJLLJLLJLLJLLJLLJLLIO";
        Result<CoachName> IDCreationResult = CoachName.fromString(validId);
        Assertions.assertThat(IDCreationResult.isSuccess()).isFalse();
        Assertions.assertThat(IDCreationResult.getError().getType()).isEqualTo(FORMAT_VALIDATION_ERROR);
        Assertions.assertThat(IDCreationResult.getError().getMessage()).isEqualTo("CoachName must be alphanumeric and 1-50 characters");
    }

    @Test
    void testCoachNameCreationWithBadStringFails() {
        // Test creating a CoachName with a valid ID
        String validId = "Kiki+%";
        Result<CoachName> IDCreationResult = CoachName.fromString(validId);
        Assertions.assertThat(IDCreationResult.isSuccess()).isFalse();
        Assertions.assertThat(IDCreationResult.getError().getType()).isEqualTo(FORMAT_VALIDATION_ERROR);
        Assertions.assertThat(IDCreationResult.getError().getMessage()).isEqualTo("CoachName must be alphanumeric and 1-50 characters");
    }

    @Test
    void testCoachNameWithEmptyStringFails() {
        String validId = "";
        Result<CoachName> IDCreationResult = CoachName.fromString(validId);
        Assertions.assertThat(IDCreationResult.isSuccess()).isFalse();
        Assertions.assertThat(IDCreationResult.getError().getType()).isEqualTo(FORMAT_VALIDATION_ERROR);
        Assertions.assertThat(IDCreationResult.getError().getMessage()).isEqualTo("CoachName cannot be empty");
    }

    @Test
    void testCoachNameWithNullStringFails() {
        String validId = null;
        Result<CoachName> IDCreationResult = CoachName.fromString(validId);
        Assertions.assertThat(IDCreationResult.isSuccess()).isFalse();
        Assertions.assertThat(IDCreationResult.getError().getType()).isEqualTo(FORMAT_VALIDATION_ERROR);
        Assertions.assertThat(IDCreationResult.getError().getMessage()).isEqualTo("CoachName cannot be null");
    }

    @Test
    void testCreateEntityWithCorrectLengthAndSpaces() {
        String validId = "Capitano' America";
        Result<CoachName> IDCreationResult = CoachName.fromString(validId);
        Assertions.assertThat(IDCreationResult.isSuccess()).isTrue();
        Assertions.assertThat(IDCreationResult.getValue().toString()).isEqualTo(validId);
    }

    @Test
    void testCreateEntityWithCorrectLengthAndUnderscoresSucceed() {
        String validId = "Capitano-A_merica";
        Result<CoachName> IDCreationResult = CoachName.fromString(validId);
        Assertions.assertThat(IDCreationResult.isSuccess()).isTrue();
        Assertions.assertThat(IDCreationResult.getValue().toString()).isEqualTo(validId);
    }

}
