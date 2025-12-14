package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.validators.DomainValidator;
import com.bloodbowlclub.shared.shared.cloudinary_url.CloudinaryUrl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryUrlTest {

    @Test
    @DisplayName("a valid Cloudinary URL, should be valid")
    void testValidCloudinaryUrl() {
        CloudinaryUrl goodUrl = new CloudinaryUrl("https://res.cloudinary.com/bloodbowlclub-com/image/upload/v1659445677/user_uploads/x44ke8sgvqrap91mry8i.jpg");
        boolean isValid = DomainValidator.isValid(goodUrl);
        Assertions.assertTrue(isValid);
    }

    @Test
    @DisplayName("a invalid Cloudinary URL, should be invalid")
    void testInValidCloudinaryUrl() {
        CloudinaryUrl goodUrl = new CloudinaryUrl("toto");
        boolean isValid = DomainValidator.isValid(goodUrl);
        Assertions.assertFalse(isValid);
        Map<String, String> errors = DomainValidator.getErrors(goodUrl);
        Map<String, String> expectedError = new HashMap<>();
        expectedError.put("value", "L'Url n'est pas une url Cloudinary");
        Assertions.assertEquals(expectedError, errors);
    }

    @Test
    @DisplayName("a not Cloudinary URL, should not be valid")
    void testNotCloudinaryUrl() {
        CloudinaryUrl goodUrl = new CloudinaryUrl("https://www.youtube.com/watch?v=UUSmBMWk2sI");
        boolean isValid = DomainValidator.isValid(goodUrl);
        Assertions.assertFalse(isValid);
        Map<String, String> errors = DomainValidator.getErrors(goodUrl);
        Map<String, String> expectedError = new HashMap<>();
        expectedError.put("value", "L'Url n'est pas une url Cloudinary");
        Assertions.assertEquals(expectedError, errors);
    }
}
