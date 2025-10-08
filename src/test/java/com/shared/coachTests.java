package com.shared;

import com.auth.domain.user_account.values.Email;
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

    DateService ds = new DateService();
    IdService ids = new IdService();

    @Test
    public void test_without_error_is_valid() {
        Coach coach = new Coach(
                new CoachID(ids.getStringId()),
                new CoachName("toto"),
                new Email("toto@gmail.com"),
                ds.now()
        );
        Assertions.assertNotNull(coach);
        Assertions.assertFalse(coach.isNotValid());
    }

    @Test
    public void test_with_many_error_is_not_valid_and_all_errors_listed() {
        Coach coach = new Coach(
                new CoachID("123456789"),
                new CoachName("toto"),
                new Email("totogmail.com"),
                ds.now()
        );
        Assertions.assertNotNull(coach);
        Assertions.assertFalse(DomainValidator.isValid(coach));
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("coachId", "Identifiant non valide, doit être un ULID");
        errorMap.put("email", "Adresse mail incorrecte");
        Assertions.assertEquals(DomainValidator.getErrors(coach), errorMap);
    }
}
