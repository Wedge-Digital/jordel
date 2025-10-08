package com.auth.domain.value_objects;

import org.junit.jupiter.api.Test;

public class PasswordTests {

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
