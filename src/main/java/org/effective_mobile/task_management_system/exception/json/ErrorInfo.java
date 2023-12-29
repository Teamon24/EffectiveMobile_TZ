package org.effective_mobile.task_management_system.exception.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.resource.json.JsonPojo;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

/**
 * Объект с информацией об ошибке.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorInfo implements JsonPojo {

    /**
     * Unix-время возникновения ошибки.
     */
    @JsonProperty private long timestamp;

    /**
     * Числовой код ошибки.
     */
    @JsonProperty private Integer status;

    /**
     * Название ошибки.
     */
    @JsonProperty private String error;

    /**
     * Класс исключения.
     */
    @JsonProperty private String exception;

    /**
     * Сообщение из исключения.
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty private String message;

    /**
     * URL упавшего запроса.
     */
    @JsonProperty private String path;

    public ResponseEntity<ErrorInfo> responseEntity() {
        return new ResponseEntity<>(this, HttpStatusCode.valueOf(this.getStatus()));
    }
}
