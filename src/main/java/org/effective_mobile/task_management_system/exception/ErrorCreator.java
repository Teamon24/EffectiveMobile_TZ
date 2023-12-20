package org.effective_mobile.task_management_system.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorCreator {

    private interface ExMessageHandler extends Function<Exception, String> {}

    private final static ExMessageHandler getLocalizedMessage = Throwable::getLocalizedMessage;
    private final static ExMessageHandler ignoreMessage = e -> "";

    /**
     * Формирует объект с информацией о возникшей ошибке.
     * @param req информация о запросе.
     * @param ex исключение.
     * @param httpStatus статус ошибки.
     * @param messageHandler логика, которая устанавливает сообщение
     * @return информация о возникшей ошибке.
     */
    public static ErrorInfo getErrorInfo(
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

    public static ErrorInfo createErrorInfo(
        HttpServletRequest req,
        Exception ex,
        HttpStatus httpStatus
    ) {
        return getErrorInfo(req, ex, httpStatus, getLocalizedMessage);
    }

    /**
     * Формирует объект с информацией о возникшей ошибке валидации.
     * @param req информация о запросе.
     * @param ex исключение.
     * @return информация о возникшей ошибке.
     */
    public static ValidationErrorInfo createValidationErrorInfo(
        HttpServletRequest req,
        MethodArgumentNotValidException ex
    ) {
        ErrorInfo errorInfo1 = getErrorInfo(req, ex, HttpStatus.BAD_REQUEST, ignoreMessage);
        return new ValidationErrorInfo(errorInfo1);
    }

    public static List<ValidationErrorInfo.ValidationError> validationErrors(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        return bindingResult
            .getFieldErrors()
            .stream()
            .map(ValidationErrorInfo.ValidationError::create)
            .toList();
    }
}
