package org.effective_mobile.task_management_system.logging;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import lombok.Builder;

/**
 * Информация для аутентификации из запроса.
 */
@Builder
public class HttpRequestAuthInfo {
    /**
     * Токен аутентификации. */
    @Nullable @JsonProperty private String token;

    /**
     * Cookie из http-запроса. */
    @JsonProperty private Cookie[] cookies = new Cookie[0];

    /**
     * Заголовки http-запроса. */
    @JsonProperty private Headers headers = Headers.empty();
}
