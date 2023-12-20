package org.effective_mobile.task_management_system.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.effective_mobile.task_management_system.security.AuthTokenComponent;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

import static org.effective_mobile.task_management_system.logging.HttpExchangeLoggingUtils.getHeaders;
import static org.effective_mobile.task_management_system.logging.HttpExchangeLoggingUtils.getPayload;

@Log4j2
@AllArgsConstructor
public class HttpExchangeLoggingInterceptor implements HandlerInterceptor {

    private final AuthTokenComponent authTokenComponent;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (log.isDebugEnabled() || log.isInfoEnabled()) {
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

            log.debug(httpRequestLogPojo.asPrettyJson(objectMapper));
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

        if (log.isDebugEnabled() || log.isInfoEnabled()) {
            try {
                final HttpResponseLogPojo httpResponseLogPojo = HttpResponseLogPojo.builder()
                    .status(response.getStatus())
                    .responseBody(getPayload(response))
                    .headers(getHeaders(response))
                    .executionTime(endTime - startTime)
                    .build();

                log.debug(httpResponseLogPojo.asPrettyJson(objectMapper));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setStartTime(HttpServletRequest request) {
        request.setAttribute(Headers.START_TIME_ATTRIBUTE, System.currentTimeMillis());
    }

    private Long getStartTime(HttpServletRequest request) {
        return (Long) request.getAttribute(Headers.START_TIME_ATTRIBUTE);
    }
}