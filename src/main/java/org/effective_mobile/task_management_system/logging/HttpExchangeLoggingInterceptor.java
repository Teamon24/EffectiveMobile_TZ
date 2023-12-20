package org.effective_mobile.task_management_system.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.effective_mobile.task_management_system.security.AuthTokenComponent;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

import static org.effective_mobile.task_management_system.logging.HttpExchangeLoggingUtils.getHeaders;
import static org.effective_mobile.task_management_system.logging.HttpExchangeLoggingUtils.getPayload;

@AllArgsConstructor
public class HttpExchangeLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LogManager.getLogger(HttpExchangeLoggingInterceptor.class);

    private final AuthTokenComponent authTokenComponent;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (logger.isDebugEnabled() || logger.isInfoEnabled()) {
            setStartTime(request);
            HttpRequestLogPojo httpRequestLogPojo = HttpRequestLogPojo.builder()
                .httpMethod(request.getMethod())
                .path(request.getRequestURI())
                .requestBody(HttpExchangeLoggingUtils.getPayload(request))
                .clientIp(request.getRemoteHost() + ":" + request.getRemotePort())
                .queryString(request.getQueryString())
                .httpRequestAuthInfo(
                    HttpRequestAuthInfo.builder()
                        .headers(getHeaders(request))
                        .cookies(request.getCookies())
                        .token(authTokenComponent.getTokenFromCookies(request))
                        .build()).build();

            logger.debug(httpRequestLogPojo.toPrettyJson(objectMapper));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex)
    {
        val startTime = getStartTime(request);
        val endTime = System.currentTimeMillis();

        if (logger.isDebugEnabled() || logger.isInfoEnabled()) {
            try {
                final HttpResponseLogPojo httpResponseLogPojo = HttpResponseLogPojo.builder()
                    .status(response.getStatus())
                    .responseBody(getPayload(response))
                    .headers(getHeaders(response))
                    .executionTime(endTime - startTime)
                    .build();

                logger.debug(httpResponseLogPojo.toPrettyJson(objectMapper));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setStartTime(HttpServletRequest request) {
        request.setAttribute(Headers.START_TIME_HEADER, System.currentTimeMillis());
    }

    private Long getStartTime(HttpServletRequest request) {
        return (Long) request.getAttribute(Headers.START_TIME_HEADER);
    }
}