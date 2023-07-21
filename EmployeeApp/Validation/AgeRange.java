package com.example.EmployeeApp.Validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.time.LocalDate;

@Documented
@Constraint(validatedBy = AgeRangeValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AgeRange {
    String message() default "Invalid age";
    int min() default 18;
    int max() default 120;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
