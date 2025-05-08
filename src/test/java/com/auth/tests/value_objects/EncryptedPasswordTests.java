package com.auth.tests.value_objects;

import com.auth.domain.CoachName;
import com.auth.domain.EncryptedPassword;
import com.auth.services.Result;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.auth.services.errors.ErrorType.FORMAT_VALIDATION_ERROR;

public class EncryptedPasswordTests {

    @Test
    void testCoachNameCreationWithBadStringFails() {
        String validId = "coincoin";
        Result<EncryptedPassword> CreationResult = EncryptedPassword.fromString(validId);
        Assertions.assertThat(CreationResult.isSuccess()).isFalse();
        Assertions.assertThat(CreationResult.getError().getType()).isEqualTo(FORMAT_VALIDATION_ERROR);
        Assertions.assertThat(CreationResult.getError().getMessage()).isEqualTo("Encrypted password is not a valid encrypted string");
    }

    @Test
    void testCoachNameCreationWithStringLentghtOKButIncorrectFails() {
        // Test creating a CoachName with a valid ID
        String validId = "coincoin-coincoin-coincoin-coincoincoincoin-coincoin-coincoi";
        Result<EncryptedPassword> CreationResult = EncryptedPassword.fromString(validId);
        Assertions.assertThat(CreationResult.isSuccess()).isFalse();
        Assertions.assertThat(CreationResult.getError().getType()).isEqualTo(FORMAT_VALIDATION_ERROR);
        Assertions.assertThat(CreationResult.getError().getMessage()).isEqualTo("Encrypted password is not a valid encrypted string");
    }

    @Test
    void testEncryptedPasswordCreationSucceed() {
        String validId = "$2a$12$WwdWTE5g9Ni16nxK90mamu8asuYWLhWKhg6MV96999AIGFRsCN262";
        Result<EncryptedPassword> IDCreationResult = EncryptedPassword.fromString(validId);
        Assertions.assertThat(IDCreationResult.isSuccess()).isTrue();
    }

    @Test
    void testCoachNameWithNullStringFails() {
        String validId = null;
        Result<EncryptedPassword> IDCreationResult = EncryptedPassword.fromString(validId);
        Assertions.assertThat(IDCreationResult.isSuccess()).isFalse();
        Assertions.assertThat(IDCreationResult.getError().getType()).isEqualTo(FORMAT_VALIDATION_ERROR);
        Assertions.assertThat(IDCreationResult.getError().getMessage()).isEqualTo("Encrypted Password cannot be null");
    }

    @Test
    void testCoachNameWithEmptyStringFails() {
        String validId = "";
        Result<EncryptedPassword> IDCreationResult = EncryptedPassword.fromString(validId);
        Assertions.assertThat(IDCreationResult.isSuccess()).isFalse();
        Assertions.assertThat(IDCreationResult.getError().getType()).isEqualTo(FORMAT_VALIDATION_ERROR);
        Assertions.assertThat(IDCreationResult.getError().getMessage()).isEqualTo("Encrypted Password cannot be empty");
    }

    @Test
    void testCreateEntityWithCorrectLengthAndSpaces() {
        String validId = "$2a$12$WwdWTE5g9Ni16nxK90mamu8asuYWLhWKhg6MV96999AIGFRsCN262";
        Result<EncryptedPassword> IDCreationResult = EncryptedPassword.fromString(validId);
        Assertions.assertThat(IDCreationResult.isSuccess()).isTrue();
        Assertions.assertThat(IDCreationResult.getValue().matches("toto l'asticot coin coin")).isTrue();
    }


}
