package org.smg.carlisting.domain.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class CarListingYearValidatorTest {

    @InjectMocks
    private CarListingYearValidator carListingYearValidator;

    @Test
    public void testIsValidWhenNegative() {
        assertFalse(carListingYearValidator.isValid(-1990, null));
    }

    @Test
    public void testIsValidWhenInFuture() {
        int nextYear = Year.now().getValue() + 1;
        assertFalse(carListingYearValidator.isValid(nextYear, null));
    }

    @Test
    public void testIsValidWhenValid() {
        assertTrue(carListingYearValidator.isValid(Year.now().getValue(), null));
    }
}