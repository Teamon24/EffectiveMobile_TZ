package org.effective_mobile.task_management_system.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;
import org.effective_mobile.task_management_system.confing.JacksonConfig;
import org.effective_mobile.task_management_system.logging.Headers.Header;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpExchangeLoggingUtils {

    public static Headers getHeaders(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames()).stream()
            .map(getHeader(request))
            .collect(Headers.pairsToHeaders);
    }

    public static Headers getHeaders(HttpServletResponse response) {
        return response.getHeaderNames().stream()
            .map(getHeader(response))
            .collect(Headers.pairsToHeaders);
    }

    public static Function<String, Header> getHeader(HttpServletRequest request) {
        return headerName -> {
            final String headerValue = request.getHeader(headerName);
            return Header.of(headerName, headerValue);
        };
    }

    public static Function<String, Header> getHeader(HttpServletResponse response) {
        return headerName -> {
            final String headerValue = response.getHeader(headerName);
            return Header.of(headerName, headerValue);
        };
    }

    public static Collector<Header, ?, Map<String, String>> toMap() {
        return Collectors.toMap(Pair::getKey, Pair::getValue);
    }

    /**
     * @param request http запрос.
     * @return данные, передаваемые в теле запроса.
     */
    public static Object getPayload(@NonNull final HttpServletRequest request) {
        ContentCachingRequestWrapper requestWrapper = WebUtils
            .getNativeRequest(request, ContentCachingRequestWrapper.class);

        Object payload = null;
        if (null != requestWrapper) {
            byte[] buf = requestWrapper.getContentAsByteArray();
            String characterEncoding = requestWrapper.getCharacterEncoding();
            payload = getPayload(buf, characterEncoding, payload);
        }
        return payload;
    }

    /**
     * @param response http-ответ.
     * @return данные, передаваемые в теле ответа.
     */
    public static Object getPayload(@NonNull final HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper responseWrapper = WebUtils
            .getNativeResponse(response, ContentCachingResponseWrapper.class);

        Object payload = null;
        if (null != responseWrapper) {
            byte[] buf = responseWrapper.getContentAsByteArray();
            String characterEncoding = responseWrapper.getCharacterEncoding();
            responseWrapper.copyBodyToResponse();
            payload = getPayload(buf, characterEncoding, payload);
        }
        return payload;
    }

    /**
     * @param data строка с данными.
     * @return если строка с данными является валидным json-ом, возвращает распарсенный объект по json-у,
     * иначе возвращает заданную строку без изменения.
     */
    public static Object toObject(final String data) {
        if (null != data && !data.equals("")) {
            ObjectMapper mapper = JacksonConfig.getObjectMapperInstance();
            try {
                return mapper.readValue(data, Object.class);
            } catch (IOException e) {
                return data;
            }
        } else {
            return data;
        }
    }

    private static Object getPayload(byte[] buf, String characterEncoding, Object payload) {
        if (buf.length > 0) {
            String payloadStr;
            try {
                payloadStr = new String(buf, 0, buf.length, characterEncoding);
            } catch (UnsupportedEncodingException ex) {
                payloadStr = "[unknown]";
            }
            payload = toObject(payloadStr);
        }
        return payload;
    }
}
