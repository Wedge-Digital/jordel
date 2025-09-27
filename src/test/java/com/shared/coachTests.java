package com.shared;

import com.auth.domain.Email;
import com.shared.services.DateService;
import com.shared.coach.CoachID;
import com.shared.coach.CoachName;
import com.shared.coach.Coach;
import com.shared.services.IdService;
import com.shared.validators.DomainValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class coachTests {

    @Test
    public void test_without_error_is_valid() {
        Coach coach = new Coach(
                new CoachID(IdService.getStringId()),
                new CoachName("toto"),
                new Email("toto@gmail.com"),
                DateService.now()
        );
        Assertions.assertNotNull(coach);
        Assertions.assertTrue(DomainValidator.isValid(coach));
    }

    @Test
    public void test_with_many_error_is_not_valid_and_all_errors_listed() {
        Coach coach = new Coach(
                new CoachID("123456789"),
                new CoachName("toto"),
                new Email("totogmail.com"),
                DateService.now()
        );
        Assertions.assertNotNull(coach);
        Assertions.assertFalse(DomainValidator.isValid(coach));
        Map<String, List<String>> errorMap = new HashMap<>();
        errorMap.put("coachId.value", List.of("must be alphanumeric and 26 characters", "is not a valid ULID"));
        errorMap.put("email.value", List.of("doit être une adresse électronique syntaxiquement correcte"));
        Assertions.assertEquals(DomainValidator.getErrors(coach), errorMap);
    }
}
