package org.effective_mobile.task_management_system.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueSingupValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Signup {
    String message() default "{validation.error.signup.taken}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Type field();

    enum Type { USERNAME, EMAIL }
}