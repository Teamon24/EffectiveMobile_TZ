package org.effective_mobile.task_management_system.component.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.effective_mobile.task_management_system.utils.enums.ValuableEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnumAtRequestBodyValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidEnum {
    Class<? extends ValuableEnum<String>> clazz();

    String message() default "{validation.error.enum.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}