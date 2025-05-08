package com.auth.tests.value_objects;

import com.auth.domain.EmailAddress;
import com.auth.domain.EntityID;
import com.auth.services.Result;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Null;
import ulid4j.Ulid;

import static com.auth.services.errors.ErrorType.FORMAT_VALIDATION_ERROR;

public class EmailTests {

    @Test
    void TestCreateEmailAddressFromEmptyStringFails() {
        Result<EmailAddress> emailAddress = EmailAddress.fromString("");
        Assertions.assertThat(emailAddress.isSuccess()).isFalse();
        Assertions.assertThat(emailAddress.getError().getType().isValidationError()).isTrue();
        Assertions.assertThat(emailAddress.getError().getType()).isEqualTo(FORMAT_VALIDATION_ERROR);
        Assertions.assertThat(emailAddress.getError().getMessage()).isEqualTo("Email address cannot be empty");
    }

    @Test
    void TestCreateEmailAddressFromNullStringFails() {
        Result<EmailAddress> emailAddress = EmailAddress.fromString(null);
        Assertions.assertThat(emailAddress.isSuccess()).isFalse();
        Assertions.assertThat(emailAddress.getError().getType().isValidationError()).isTrue();
        Assertions.assertThat(emailAddress.getError().getType()).isEqualTo(FORMAT_VALIDATION_ERROR);
        Assertions.assertThat(emailAddress.getError().getMessage()).isEqualTo("Email address cannot be null");
    }

    @Test
    void TestCreateEmailAddressFromWrongStringFails() {
        Result<EmailAddress> emailAddress = EmailAddress.fromString("invalid-email");
        Assertions.assertThat(emailAddress.isSuccess()).isFalse();
        Assertions.assertThat(emailAddress.getError().getType().isValidationError()).isTrue();
        Assertions.assertThat(emailAddress.getError().getType()).isEqualTo(FORMAT_VALIDATION_ERROR);
        Assertions.assertThat(emailAddress.getError().getMessage()).isEqualTo("Bad format for email address");
    }

    @Test
    void TestCreateEmailAddressSucceed() {
        Result<EmailAddress> emailAddress = EmailAddress.fromString("tota@gmail.com");
        Assertions.assertThat(emailAddress.isSuccess()).isTrue();
        Assertions.assertThat(emailAddress.getValue().toString()).isEqualTo("tota@gmail.com");
    }

}
