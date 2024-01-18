package org.effective_mobile.task_management_system.component.validator.smart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * Глобальный класс, который регистрирует валидацию для всех контроллеров.
 */
@ControllerAdvice
public class ValidationAdvice {

    /**
     * Выбирает валидатор из некоторого каталога валидаторов и валидирует DTO. */
    @Autowired
    private SmartValidationComponent validationForwarder;

    /**
     * Регистрирует custom-ные валидаторы для получаемого с fronend-а json pojo.
     * @param binder объект для настройки связи между параметрами web-запроса и JavaBean объектами.
     */
    @InitBinder
    protected void initBinder(final WebDataBinder binder) {
        binder.setValidator(this.validationForwarder);
    }
}