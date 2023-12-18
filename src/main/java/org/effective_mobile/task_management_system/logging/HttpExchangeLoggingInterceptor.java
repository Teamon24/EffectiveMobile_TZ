package org.effective_mobile.task_management_system.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.effective_mobile.task_management_system.security.TokenComponent;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

import static org.effective_mobile.task_management_system.logging.HttpExchangeLoggingUtils.getHeaders;
import static org.effective_mobile.task_management_system.logging.HttpExchangeLoggingUtils.getPayload;

public class HttpExchangeLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LogManager.getLogger(HttpExchangeLoggingInterceptor.class);

    private TokenComponent tokenComponent;

    private ObjectMapper objectMapper;

    public HttpExchangeLoggingInterceptor(
        TokenComponent tokenComponent,
        ObjectMapper objectMapper
    ) {
        this.tokenComponent = tokenComponent;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (logger.isDebugEnabled() || logger.isInfoEnabled()) {
            response.setHeader(Headers.START_TIME_HEADER, String.valueOf(System.currentTimeMillis()));
            HttpRequestInfo httpRequestInfo = HttpRequestInfo.builder()
                .httpMethod(request.getMethod())
                .path(request.getRequestURI())
                .requestBody(HttpExchangeLoggingUtils.getPayload(request))
                .clientIp(request.getRemoteAddr())
                .queryString(request.getQueryString())
                .httpRequestAuthInfo(
                    HttpRequestAuthInfo.builder()
                        .headers(getHeaders(request))
                        .cookies(request.getCookies())
                        .token(tokenComponent.getTokenFromCookies(request))
                        .build()
                )
                .build();


            log(httpRequestInfo);

        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex)
    {
        val startTime = Long.parseLong(response.getHeader(Headers.START_TIME_HEADER));
        val endTime = System.currentTimeMillis();

        if (logger.isDebugEnabled() || logger.isInfoEnabled()) {
            try {
                final HttpResponseInfo httpResponseInfo = HttpResponseInfo.builder()
                    .status(response.getStatus())
                    .responseBody(getPayload(response))
                    .headers(getHeaders(response))
                    .executionTime(endTime - startTime)
                    .build();

                log(httpResponseInfo);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void log(Object object)  {
        try {
            logger.debug(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object));
        } catch (JsonProcessingException e) {
            logger.error("ObjectMapper can't serialize object: %s".formatted(object), e);
        }
    }
}