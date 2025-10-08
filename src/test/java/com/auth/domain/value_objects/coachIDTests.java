package com.auth.domain.value_objects;

import com.shared.coach.CoachID;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class coachIDTests {

    @Test
    void testEntityIDCreationWithBadStringFails() {
        String validId = "1234567890";
        CoachID coachID = new CoachID(validId);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<CoachID>> violations = validator.validate(coachID);
        List<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        Assertions.assertThat(messages).contains("Identifiant non valide, doit être un ULID");
    }
}
