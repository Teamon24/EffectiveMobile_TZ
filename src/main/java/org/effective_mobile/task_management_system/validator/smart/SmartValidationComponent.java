package org.effective_mobile.task_management_system.validator.smart;

import org.springframework.validation.SmartValidator;

/**
 * Выбирает валидатор из некоторого каталога валидаторов и валидирует DTO.
 */
public interface SmartValidationComponent extends SmartValidator {
}
