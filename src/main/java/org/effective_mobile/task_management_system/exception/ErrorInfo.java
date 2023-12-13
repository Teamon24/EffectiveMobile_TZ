package org.effective_mobile.task_management_system.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Объект с информацией об ошибке.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorInfo {

    /**
     * Unix-время возникновения ошибки.
     */
    private long timestamp;

    /**
     * Числовой код ошибки.
     */
    private Integer status;

    /**
     * Название ошибки.
     */
    private String error;

    /**
     * Класс исключения.
     */
    private String exception;

    /**
     * Сообщение из исключения.
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String message;

    /**
     * URL упавшего запроса.
     */
    private String path;
}
