package com.auth.tests.value_objects;

import com.auth.domain.EncryptedPassword;
import com.auth.services.Result;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.auth.services.errors.ErrorType.FORMAT_VALIDATION_ERROR;

public class EncryptedPasswordTests {

    @Test
    void testCoachNameCreationWithBadStringFails() {
        String validId = "coincoin";
    }

    @Test
    void testCoachNameCreationWithStringLentghtOKButIncorrectFails() {
        // Test creating a CoachName with a valid ID
        String validId = "coincoin-coincoin-coincoin-coincoincoincoin-coincoin-coincoi";
    }

    @Test
    void testEncryptedPasswordCreationSucceed() {
        String validId = "$2a$12$WwdWTE5g9Ni16nxK90mamu8asuYWLhWKhg6MV96999AIGFRsCN262";
    }

    @Test
    void testCoachNameWithNullStringFails() {
        String validId = null;
    }

    @Test
    void testCoachNameWithEmptyStringFails() {
        String validId = "";
    }

    @Test
    void testCreateEntityWithCorrectLengthAndSpaces() {
        String validId = "$2a$12$WwdWTE5g9Ni16nxK90mamu8asuYWLhWKhg6MV96999AIGFRsCN262";
    }


}
