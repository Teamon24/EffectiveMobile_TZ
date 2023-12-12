package org.effective_mobile.task_management_system.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Глобальный перехватчик исключений, формирует ответы с http-кодами ошибок для клиента.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Записывает возникающие ошибки в лог. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Писать ли в лог ошибки, обрабатываемые данным перехватчиком. */
    @Value("${app.log.http.errors.enabled}")
    private Boolean httpErrorsLogEnable;

    private interface ExMessageHandler extends Function<Exception, String> {}

    private final ExMessageHandler getLocalizedMessage = Throwable::getLocalizedMessage;
    private final ExMessageHandler ignoreMessage = e -> "";

    /**
     * 400, Bad request.
     * @param req информация о запросе.
     * @param ex исключение.
     * @param userDetails информация об аутентифицированном пользователе.
     * @return информация о возникшей ошибке.
     */
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        ToEnumConvertException.class,
        AssignmentException.class,
        IllegalStatusChangeException.class,
        UserAlreadyExistsException.class,
        TaskHasNoExecutorException.class,
        NothingToUpdateInTaskException.class
    })
    public ErrorInfo toBadRequest(
        HttpServletRequest req,
        Exception ex,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        return createErrorInfo(req, ex, HttpStatus.BAD_REQUEST, userDetails);
    }

    /**
     * 401, Unauthorized.
     * @param req информация о запросе.
     * @param ex исключение.
     * @param userDetails информация об аутентифицированном пользователе.
     * @return информация о возникшей ошибке.
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({ InvalidTokenException.class })
    public ErrorInfo toUnauthorized(
        HttpServletRequest req,
        InvalidTokenException ex,
        @AuthenticationPrincipal final UserDetails userDetails
    ) {
        return createErrorInfo(req, ex, HttpStatus.UNAUTHORIZED, userDetails);
    }

    /**
     * 403, Forbidden.
     * @param req информация о запросе.
     * @param ex исключение.
     * @param userDetails информация об аутентифицированном пользователе.
     * @return информация о возникшей ошибке.
     */
    @ResponseBody
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ExceptionHandler({
        AccessDeniedException.class,
        DeniedOperationException.class
    })
    public ErrorInfo toForbidden(
        HttpServletRequest req,
        Exception ex,
        @AuthenticationPrincipal final UserDetails userDetails
    ) {
        return createErrorInfo(req, ex, HttpStatus.FORBIDDEN, userDetails);
    }

    /**
     * 404, Not found.
     * @param req информация о запросе.
     * @param ex исключение.
     * @param userDetails информация об аутентифицированном пользователе.
     * @return информация о возникшей ошибке.
     */
    @ResponseBody
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorInfo toNotFound(
        HttpServletRequest req,
        Exception ex,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        return createErrorInfo(req, ex, HttpStatus.NOT_FOUND, userDetails);
    }

    /**
     * 422, Unprocessable Entity.
     * @param req информация о запросе.
     * @param ex исключение.
     * @param userDetails информация об аутентифицированном пользователе.
     * @return информация о возникшей ошибке.
     */
    @ResponseBody
    @ExceptionHandler({
        ConversionFailedException.class,
        MethodArgumentTypeMismatchException.class
    })
    public ErrorInfo toUnprocessableEntity(
        HttpServletRequest req,
        Exception ex,
        @AuthenticationPrincipal final UserDetails userDetails
    ) {
        Exception exception = ex;
        if (ex instanceof MethodArgumentTypeMismatchException) {
            exception = (Exception) ex.getCause();
        }

        return createErrorInfo(req, exception, HttpStatus.UNPROCESSABLE_ENTITY, userDetails);
    }

    /**
     * 400, Bad request, ошибки валидации.
     * @param req информация о запросе.
     * @param ex исключение.
     * @param userDetails информация об аутентифицированном пользователе.
     * @return информация о возникшей ошибке.
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ValidationErrorInfo handleValidationErrors(
        final HttpServletRequest req,
        final MethodArgumentNotValidException ex,
        @AuthenticationPrincipal final UserDetails userDetails)
    {
        ValidationErrorInfo validationErrorInfo = createValidationErrorInfo(req, ex, userDetails);
        validationErrorInfo.setErrors(validationErrors(ex));
        return validationErrorInfo;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ValidationErrorInfo exceptionHandler(final HttpServletRequest req,
                                                final MethodArgumentNotValidException ex,
                                                @AuthenticationPrincipal final UserDetails userDetails) {
        ValidationErrorInfo validationErrorInfo = createValidationErrorInfo(req, ex, userDetails);
        validationErrorInfo.setErrors(validationErrors(ex));
        return validationErrorInfo;
    }

    private ErrorInfo createErrorInfo(
        HttpServletRequest req,
        Exception ex,
        HttpStatus httpStatus,
        UserDetails userDetails
    ) {
        ErrorInfo errorInfo = this.getErrorInfo(req, ex, httpStatus, getLocalizedMessage);
        this.writeLog(ex, userDetails);
        return errorInfo;
    }

    /**
     * Формирует объект с информацией о возникшей ошибке валидации.
     * @param req информация о запросе.
     * @param ex исключение.
     * @return информация о возникшей ошибке.
     */
    private ValidationErrorInfo createValidationErrorInfo(
        HttpServletRequest req,
        MethodArgumentNotValidException ex,
        UserDetails userDetails
    ) {
        ErrorInfo errorInfo1 = this.getErrorInfo(req, ex, HttpStatus.BAD_REQUEST, ignoreMessage);
        ValidationErrorInfo validationErrorInfo = new ValidationErrorInfo(errorInfo1);
        this.writeLog(ex, userDetails);
        return validationErrorInfo;
    }

    private List<ValidationError> validationErrors(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        return bindingResult
            .getFieldErrors()
            .stream()
            .map(ValidationError::create)
            .toList();
    }

    /**
     * Формирует объект с информацией о возникшей ошибке.
     * @param req информация о запросе.
     * @param ex исключение.
     * @param httpStatus статус ошибки.
     * @param messageHandler логика, которая устанавливает сообщение
     * @return информация о возникшей ошибке.
     */
    private ErrorInfo getErrorInfo(
        final HttpServletRequest req,
        final Exception ex,
        final HttpStatus httpStatus,
        ExMessageHandler messageHandler
    ) {
        final String userLogMessage = messageHandler.apply(ex);

        return new ErrorInfo(
            new Date().getTime(),               // unix-время возникновения ошибки
            httpStatus.value(),                 // код ошибки
            httpStatus.getReasonPhrase(),       // имя ошибки
            ex.getClass().getCanonicalName(),   // полное имя класса-исключения
            userLogMessage,                     // сообщение исключения
            req.getRequestURI()                 // относительный путь до запрашиваемого rest-метода
        );
    }

    /**
     * Записывает информацию о возникшей ошибке в лог.
     * @param ex исключение.
     * @param userDetails информация об аутентифицированном пользователе.
     */
    private void writeLog(Exception ex, final UserDetails userDetails) {
        if (this.httpErrorsLogEnable) {
            String message = ex.getLocalizedMessage();
            message += getUserInfo(userDetails);
            LOGGER.error(message, ex);
        }
    }

    /**
     * @param userDetails информация об аутентифицированном пользователе.
     * @return данные о пользователе, либо пустая строка если данных нет.
     */
    private String getUserInfo(UserDetails userDetails) {
        if (null != userDetails) {
            Collection<? extends GrantedAuthority> role = userDetails.getAuthorities();
            return String.format("%n[user: %s, auth = (%s)]",
                userDetails.getUsername(),
                role.stream().map(Object::toString).collect(Collectors.toList()));
        } else {
            return "user info is absent";
        }
    }

    /**
     * Объект с информацией об ошибке.
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorInfo {

        /**
         * Unix-время возникновения ошибки. */
        private long timestamp;

        /**
         * Числовой код ошибки. */
        private Integer status;

        /**
         * Название ошибки. */
        private String error;

        /**
         * Класс исключения. */
        private String exception;

        /**
         * Сообщение из исключения. */
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String message;

        /**
         * URL упавшего запроса. */
        private String path;
    }

    @Data
    @Builder
    public static class ValidationError {
        private String object;
        private String field;
        private Object rejectedValue;
        private String message;

        public static ValidationError create(FieldError fieldError) {
            return ValidationError.builder()
                .object(fieldError.getObjectName())
                .field(fieldError.getField())
                .rejectedValue(fieldError.getRejectedValue())
                .message(fieldError.getDefaultMessage())
                .build();
        }
    }

    /**
     * Объект, дополнительно содержащий информацию об ошибках валидации.
     */
    public static class ValidationErrorInfo extends ErrorInfo {

        /**
         * Набор ошибок с сообщениями.
         */
        @Getter
        @Setter
        private List<ValidationError> errors;

        /**
         * Строит объект, получая информацию из существующего {@link ErrorInfo}.
         *
         * @param errorInfo {@link ErrorInfo}.
         */
        ValidationErrorInfo(ErrorInfo errorInfo) {
            super(
                errorInfo.getTimestamp(),
                errorInfo.getStatus(),
                errorInfo.getError(),
                errorInfo.getException(),
                errorInfo.getMessage(),
                errorInfo.getPath()
            );
        }
    }
}

