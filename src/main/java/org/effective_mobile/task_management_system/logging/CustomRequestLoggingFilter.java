package org.effective_mobile.task_management_system.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.effective_mobile.task_management_system.component.ContextComponent;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

/**
 * Перехватывает все запросы и логгирует http-запрос и http-ответ если включено http-логгирование.
 */
@Component
@AllArgsConstructor
public class CustomRequestLoggingFilter extends CommonsRequestLoggingFilter {

    private static final Logger logger = LogManager.getLogger("resources");

    /**
     * Вытаскивает инфу для аутентификации из запроса.
     */
    private final ContextComponent contextComponent;

    @Override
    protected void doFilterInternal(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain filterChain
    )
        throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        val startTime = System.currentTimeMillis();
        try {
            super.doFilterInternal(requestWrapper, responseWrapper, filterChain);

        } finally {
            val endTimeTime = System.currentTimeMillis();
            byte[] responseArray = responseWrapper.getContentAsByteArray();
            String responseStr = new String(responseArray, responseWrapper.getCharacterEncoding());
            System.out.println("string" + responseStr);
            /*It is important to copy cached reponse body back to response stream
            to see response */
            responseWrapper.copyBodyToResponse();
        }
    }
}
