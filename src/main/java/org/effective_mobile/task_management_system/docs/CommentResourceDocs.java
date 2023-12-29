package org.effective_mobile.task_management_system.docs;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.effective_mobile.task_management_system.exception.json.ErrorInfo;
import org.effective_mobile.task_management_system.resource.Api;
import org.effective_mobile.task_management_system.resource.json.CommentCreationRequestPojo;
import org.effective_mobile.task_management_system.security.CustomUserDetails;

public interface CommentResourceDocs {

    @Tag(name = "Создание комментария")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            content = {@Content(schema = @Schema(implementation = Long.class))}),
        @ApiResponse(
            responseCode = "404",
            content = {@Content(schema = @Schema(implementation = ErrorInfo.class))},
            description = "При отсутствии задачи в базе данных."
        )
    })
    Long createComment(
        @Parameter(in = ParameterIn.PATH, name = Api.PathParam.COMMENT_TASK_ID) Long taskId,
        @RequestBody CommentCreationRequestPojo commentCreationRequestPojo,
        CustomUserDetails customUserDetails
    );
}
