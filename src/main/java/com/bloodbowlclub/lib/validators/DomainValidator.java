package com.bloodbowlclub.lib.validators;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DomainValidator {

    public static <T> boolean isValid (T instanceToCheck) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(instanceToCheck);
        List<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        return messages.isEmpty();
    }

    public static <T> Map<String, String> getErrors(T instanceToCheck) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<T>> violations = validator.validate(instanceToCheck);

        return violations.stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString().split("\\.")[0],
                        ConstraintViolation::getMessage,
                        (msg1, msg2) -> msg1 + "; " + msg2 // en cas de doublons, concaténer les messages
                ));
    }




}
