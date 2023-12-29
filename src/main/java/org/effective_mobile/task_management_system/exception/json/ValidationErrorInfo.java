package org.effective_mobile.task_management_system.exception.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.FieldError;

import java.util.List;

/**
 * Объект, дополнительно содержащий информацию об ошибках валидации.
 */
public class ValidationErrorInfo extends ErrorInfo {

    /**
     * Набор ошибок с сообщениями.
     */
    @Getter
    @Setter
    @JsonProperty private List<ValidationError> errors;

    /**
     * Строит объект, получая информацию из существующего {@link ErrorInfo}.
     *
     * @param errorInfo {@link ErrorInfo}.
     */
    public ValidationErrorInfo(ErrorInfo errorInfo) {
        super(
            errorInfo.getTimestamp(),
            errorInfo.getStatus(),
            errorInfo.getError(),
            errorInfo.getException(),
            errorInfo.getMessage(),
            errorInfo.getPath()
        );
    }

    @Data
    @Builder
    public static class ValidationError {
        @JsonProperty private String object;
        @JsonProperty private String field;
        @JsonProperty private Object rejectedValue;
        @JsonProperty private String message;

        public static ValidationError create(FieldError fieldError) {
            return ValidationError.builder()
                .object(fieldError.getObjectName())
                .field(fieldError.getField())
                .rejectedValue(fieldError.getRejectedValue())
                .message(fieldError.getDefaultMessage())
                .build();
        }
    }
}
