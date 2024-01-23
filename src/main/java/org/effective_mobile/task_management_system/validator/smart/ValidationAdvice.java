package org.effective_mobile.task_management_system.validator.smart;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * Глобальный класс, который регистрирует валидацию для всех контроллеров.
 */
@ControllerAdvice
@AllArgsConstructor
public class ValidationAdvice {

    /**
     * Выбирает валидатор из каталога валидаторов {@link SmartValidationComponent} и валидирует DTO. */
    private final SmartValidationComponent smartValidationComponent;

    /**
     * Регистрирует custom-ные валидаторы для получаемого с fronend-а json pojo.
     * @param binder объект для настройки связи между параметрами web-запроса и JavaBean объектами.
     */
    @InitBinder
    protected void initBinder(final WebDataBinder binder) {
        binder.setValidator(this.smartValidationComponent);
    }
}