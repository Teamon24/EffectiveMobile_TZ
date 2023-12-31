package org.effective_mobile.task_management_system.docs;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.effective_mobile.task_management_system.exception.json.ErrorInfo;
import org.effective_mobile.task_management_system.resource.json.CommentCreationRequestPojo;
import org.effective_mobile.task_management_system.security.CustomUserDetails;
import org.springframework.http.MediaType;

public interface CommentResourceDocs {
    @Tag(name = "Создание комментария")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            content = {@Content(
                mediaType = MediaType.TEXT_PLAIN_VALUE,
                schema = @Schema(implementation = Long.class))}),
        @ApiResponse(
            responseCode = "404",
            content = {@Content(schema = @Schema(implementation = ErrorInfo.class))},
            description = "При отсутствии задачи в базе данных."
        )
    })
    Long createComment(
        @RequestBody CommentCreationRequestPojo commentCreationRequestPojo,
        CustomUserDetails customUserDetails
    );
}
