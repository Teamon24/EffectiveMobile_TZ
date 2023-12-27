package org.effective_mobile.task_management_system.maintain.logging;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.Cookie;
import lombok.Builder;

/**
 * Информация для аутентификации из запроса.
 */
@Builder
public class HttpRequestAuthInfo implements HttpExchangeLogPojo {
    /**
     * Токен аутентификации. */
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String token;

    /**
     * Cookie из http-запроса. */
    @JsonProperty private Cookie[] cookies = new Cookie[0];

    /**
     * Заголовки http-запроса. */
    @JsonProperty private Headers headers = Headers.empty();
}
