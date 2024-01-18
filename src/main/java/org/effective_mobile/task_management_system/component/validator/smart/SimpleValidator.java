package org.effective_mobile.task_management_system.component.validator.smart;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

@AllArgsConstructor
public abstract class SimpleValidator<T> implements SmartValidator {

    protected final SmartValidator smartValidator;

    protected abstract Class <T> getValidatableClass();

    protected abstract void simplyValidate(T target, Errors errors, Object... validationHints);

    @Override
    public void validate(@NonNull Object target,
                         @NonNull Errors errors,
                         Object @NonNull ... validationHints
    ) {
        T cast = getValidatableClass().cast(target);
        this.smartValidator.validate(target, errors, validationHints);
        simplyValidate(cast, errors, validationHints);
    }

    @Override
    public void validate(@NonNull Object target,
                         @NonNull Errors errors
    ) {
        this.validate(target, errors, new Object[] {});
    }

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return getValidatableClass() == clazz;
    }
}
