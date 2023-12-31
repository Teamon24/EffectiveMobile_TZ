package org.effective_mobile.task_management_system.maintain.docs;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.effective_mobile.task_management_system.exception.json.ErrorInfo;
import org.effective_mobile.task_management_system.exception.json.ValidationErrorInfo;
import org.effective_mobile.task_management_system.resource.json.UserCreationResponsePojo;
import org.effective_mobile.task_management_system.resource.json.auth.SigninRequestPojo;
import org.effective_mobile.task_management_system.resource.json.auth.SigninResponsePojo;
import org.effective_mobile.task_management_system.resource.json.auth.SignupRequestPojo;

public interface AuthenticationResourceDocs {

    @Tag(name = "Регистрация")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            content = { @Content(schema = @Schema(implementation = UserCreationResponsePojo.class)) }
        ),
        @ApiResponse(
            responseCode = "400",
            description =
                """
                    - имя пользователя занято;
                    - почта пользователя занята;
                    - почта имеет неверный формат;
                    - пароль имеет неверный формат;
                """,
            content = { @Content(schema = @Schema(implementation = ValidationErrorInfo.class)) })
    })
    UserCreationResponsePojo signup(SignupRequestPojo signUpPayload);

    @Tag(name = "Вход в систему")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            content = { @Content(schema = @Schema(implementation = SigninResponsePojo.class)) }),
        @ApiResponse(
            responseCode = "401",
            description = "Аутентификация не пройдена.",
            content = { @Content(schema = @Schema(implementation = ErrorInfo.class)) }),
        @ApiResponse(
            responseCode = "404",
            description = "Пользователь не найден в системе.",
            content = { @Content(schema = @Schema(implementation = ErrorInfo.class)) })
    })
    SigninResponsePojo signin(SigninRequestPojo signinRequestPojo);
}
