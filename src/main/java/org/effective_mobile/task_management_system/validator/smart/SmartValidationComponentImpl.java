package org.effective_mobile.task_management_system.validator.smart;

import lombok.NonNull;
import org.effective_mobile.task_management_system.validator.smart.task.StatusChangeValidator;
import org.effective_mobile.task_management_system.validator.smart.task.TaskCreationValidator;
import org.effective_mobile.task_management_system.validator.smart.task.TaskEditionValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;

import java.util.List;

/**
 * Выбирает валидатор из некоторого сборника (каталога) валидаторов и валидирует DTO.
 */
@Component
public class SmartValidationComponentImpl implements SmartValidationComponent {

    /**
     * Кастомные валидаторы.
     */
    private final List<Validator> validators;

    public SmartValidationComponentImpl(
        SmartValidator smartValidator,
        ValuableEnumValidationComponent valuableEnumValidationComponent
    ) {
        this.validators = List.of(
            // 1. Валидаторы RequestPojos.
            // 1.1. Task's RequestPojos валидаторы.
            new TaskCreationValidator(smartValidator, valuableEnumValidationComponent),
            new TaskEditionValidator(smartValidator, valuableEnumValidationComponent),
            new StatusChangeValidator(smartValidator, valuableEnumValidationComponent),
            // 2. Стандартный валидатор - для объектов, у которых нет smart-валидатора.
            smartValidator
        );
    }

    /**
     * Проходит массив валидаторов и проверяет наличие поддержки.
     */
    @Override
    public boolean supports(final Class<?> clazz) {
        for (Validator v : validators) {
            if (v.supports(clazz)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проходит массив валидаторов и запускает валидацию, в случае, если валидатор поддерживает класс объекта.
     */
    @Override
    public void validate(final @NonNull Object target,
                         final @NonNull Errors errors) {
        this.validate(target, errors, new Object[]{});
    }

    @Override
    public void validate(final @NonNull Object target,
                         final @NonNull Errors errors,
                         final @NonNull Object ... validationHints
    ) {
        for (Validator validator : validators) {
            if (validator.supports(target.getClass())) {
                if (validator instanceof SmartValidator) {
                    ((SmartValidator) validator).validate(target, errors, validationHints);
                } else {
                    validator.validate(target, errors);
                }
                break;
            }
        }
    }
}
