package org.effective_mobile.task_management_system.logging;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class HttpResponseInfo {

    /**
     * статус http-ответа (GET, POST, ...). */
    @JsonProperty private Integer status;

    /**
     * Тело ответа. */
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Object responseBody;

    /**
     * Заголовки http-запроса. */
    @JsonProperty private Headers headers = Headers.empty();

    /**
     * Время исполнения запроса. */
    @JsonProperty private long executionTime;
}
