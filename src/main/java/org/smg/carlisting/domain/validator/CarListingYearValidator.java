package org.smg.carlisting.domain.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class CarListingYearValidator implements ConstraintValidator<CarListingYear, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value != null) {
            return value > 0 && !Year.of(value).isAfter(Year.now());
        }

        return true;
    }
}
