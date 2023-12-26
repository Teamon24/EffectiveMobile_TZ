package org.effective_mobile.task_management_system.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.effective_mobile.task_management_system.exception.ErrorCreator.createErrorInfo;
import static org.effective_mobile.task_management_system.exception.ErrorCreator.createValidationErrorInfo;

/**
 * Глобальный перехватчик исключений, формирует ответы с http-кодами ошибок для клиента.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 400, Bad request, ошибки валидации.
     * @param req информация о запросе.
     * @param ex исключение.
     * @return информация о возникшей ошибке.
     */
    @ResponseBody
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorInfo> toBadRequest(
        final HttpServletRequest req,
        final MethodArgumentNotValidException ex)
    {
        return createValidationErrorInfo(req, ex).responseEntity();
    }

    /**
     * 400, Bad request.
     * @param req информация о запросе.
     * @param ex исключение.
     * @return информация о возникшей ошибке.
     */
    @ResponseBody
    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        ToEnumConvertException.class,
        AssignmentException.class,
        IllegalStatusChangeException.class,
        UserAlreadyExistsException.class,
        TaskHasNoExecutorException.class,
        NothingToUpdateInTaskException.class
    })
    public ResponseEntity<ErrorInfo> toBadRequest(HttpServletRequest req, Exception ex) {
        return createErrorInfo(req, ex, HttpStatus.BAD_REQUEST).responseEntity();
    }

    /**
     * 401, Unauthorized.
     * @param req информация о запросе.
     * @param ex исключение.
     * @return информация о возникшей ошибке.
     */
    @ResponseBody
    @ExceptionHandler({ InvalidAuthTokenException.class })
    public ResponseEntity<ErrorInfo> toUnauthorized(HttpServletRequest req, InvalidAuthTokenException ex) {
        return createErrorInfo(req, ex, HttpStatus.UNAUTHORIZED).responseEntity();
    }

    /**
     * 403, Forbidden.
     * @param req информация о запросе.
     * @param ex исключение.
     * @return информация о возникшей ошибке.
     */
    @ResponseBody
    @ExceptionHandler({
        AccessDeniedException.class,
        DeniedOperationException.class
    })
    public ResponseEntity<ErrorInfo> toForbidden(HttpServletRequest req, Exception ex) {
        return createErrorInfo(req, ex, HttpStatus.FORBIDDEN).responseEntity();
    }

    /**
     * 404, Not found.
     * @param req информация о запросе.
     * @param ex исключение.
     * @return информация о возникшей ошибке.
     */
    @ResponseBody
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorInfo> toNotFound(HttpServletRequest req, Exception ex) {
        return createErrorInfo(req, ex, HttpStatus.NOT_FOUND).responseEntity();
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
    public ResponseEntity<ErrorInfo> toUnprocessableEntity(HttpServletRequest req, Exception ex) {
        Exception exception = ex;
        if (ex instanceof MethodArgumentTypeMismatchException) {
            exception = (Exception) ex.getCause();
        }

        return createErrorInfo(req, exception, HttpStatus.UNPROCESSABLE_ENTITY).responseEntity();
    }

    /**
     * 500, internal server error.
     * @param req информация о запросе.
     * @param ex исключение.
     * @return информация о возникшей ошибке.
     */
    @ResponseBody
    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ErrorInfo> internalServerError(
        final HttpServletRequest req,
        final Exception ex)
    {
        return createErrorInfo(req, ex, HttpStatus.INTERNAL_SERVER_ERROR).responseEntity();
    }
}