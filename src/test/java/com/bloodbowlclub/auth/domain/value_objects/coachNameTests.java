package com.bloodbowlclub.auth.domain.value_objects;

import org.junit.jupiter.api.Test;

public class coachNameTests {

    @Test
    void testCoachNameCreationWithStringTooLongFails() {
        String validId = "JLLJLLJLLJLLJLLJLLJLLJLLIOJLLJLLJLLJLLJLLJLLJLLJLLIO";
    }

    @Test
    void testCoachNameCreationWithBadStringFails() {
        // Test creating a CoachName with a valid ID
        String validId = "Kiki+%";
    }

    @Test
    void testCoachNameWithEmptyStringFails() {
        String validId = "";
    }

    @Test
    void testCoachNameWithNullStringFails() {
        String validId = null;
    }

    @Test
    void testCreateEntityWithCorrectLengthAndSpaces() {
        String validId = "Capitano' America";

    }

    @Test
    void testCreateEntityWithCorrectLengthAndUnderscoresSucceed() {
        String validId = "Capitano-A_merica";
    }

}
