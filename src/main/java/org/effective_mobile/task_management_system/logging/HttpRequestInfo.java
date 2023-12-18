package org.effective_mobile.task_management_system.logging;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Информация о http-запросе.
 */
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpRequestInfo {

    /**
     * http-метод (GET, POST, ...). */
    @JsonProperty private String httpMethod;

    /**
     * Запрашиваемый uri. */
    @JsonProperty private String path;

    /**
     * ip клиента. */
    @JsonProperty private String clientIp;

    /**
     * Тело запроса (чаще всего - принимаемый json). */
    @JsonProperty private Object requestBody;

    /**
     * Строка запроса (если есть). */
    @JsonProperty private String queryString;

    /**
     * Информация об аутентифицированном пользователе. */
    @JsonProperty private HttpRequestAuthInfo httpRequestAuthInfo;
}
