package org.effective_mobile.task_management_system.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.effective_mobile.task_management_system.exception.ErrorCreator.createErrorInfo;
import static org.effective_mobile.task_management_system.exception.ErrorCreator.createValidationErrorInfo;
import static org.effective_mobile.task_management_system.exception.ErrorCreator.validationErrors;

/**
 * Глобальный перехватчик исключений, формирует ответы с http-кодами ошибок для клиента.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 400, Bad request.
     * @param req информация о запросе.
     * @param ex исключение.
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
    public ErrorInfo toBadRequest(HttpServletRequest req, Exception ex) {

        return createErrorInfo(req, ex, HttpStatus.BAD_REQUEST);
    }

    /**
     * 401, Unauthorized.
     * @param req информация о запросе.
     * @param ex исключение.
     * @return информация о возникшей ошибке.
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({ InvalidAuthTokenException.class })
    public ErrorInfo toUnauthorized(HttpServletRequest req, InvalidAuthTokenException ex) {
        return createErrorInfo(req, ex, HttpStatus.UNAUTHORIZED);
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
    public ErrorInfo toForbidden(HttpServletRequest req, Exception ex) {
        return createErrorInfo(req, ex, HttpStatus.FORBIDDEN);
    }

    /**
     * 404, Not found.
     * @param req информация о запросе.
     * @param ex исключение.
     * @return информация о возникшей ошибке.
     */
    @ResponseBody
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorInfo toNotFound(HttpServletRequest req, Exception ex) {
        return createErrorInfo(req, ex, HttpStatus.NOT_FOUND);
    }

    /**
     * 422, Unprocessable Entity.
     * @param req информация о запросе.
     * @param ex исключение.
     * @return информация о возникшей ошибке.
     */
    @ResponseBody
    @ExceptionHandler({
        ConversionFailedException.class,
        MethodArgumentTypeMismatchException.class
    })
    public ErrorInfo toUnprocessableEntity(HttpServletRequest req, Exception ex) {
        Exception exception = ex;
        if (ex instanceof MethodArgumentTypeMismatchException) {
            exception = (Exception) ex.getCause();
        }

        return createErrorInfo(req, exception, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * 400, Bad request, ошибки валидации.
     * @param req информация о запросе.
     * @param ex исключение.
     * @return информация о возникшей ошибке.
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ValidationErrorInfo handleValidationErrors(
        final HttpServletRequest req,
        final MethodArgumentNotValidException ex)
    {
        ValidationErrorInfo validationErrorInfo = createValidationErrorInfo(req, ex);
        validationErrorInfo.setErrors(validationErrors(ex));
        return validationErrorInfo;
    }
}