package org.effective_mobile.task_management_system.resource.json.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.pojo.HasTaskInfo;
import org.effective_mobile.task_management_system.resource.json.ResponsePojo;
import org.effective_mobile.task_management_system.resource.json.comment.CommentJsonPojo;
import org.effective_mobile.task_management_system.resource.json.JsonPojoId;
import org.effective_mobile.task_management_system.utils.MiscUtils;
import org.effective_mobile.task_management_system.utils.enums.Priority;
import org.effective_mobile.task_management_system.utils.enums.Status;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor
public class TaskResponsePojo implements JsonPojoId, ResponsePojo, HasTaskInfo {

    @JsonProperty private Long id;
    @JsonProperty private Priority priority;

    @NotEmpty
    @JsonProperty private String content;
    @JsonProperty private Status status;

    @JsonProperty("creator")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TaskCreator taskCreatorJsonPojo;

    @JsonProperty("executor")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TaskExecutor taskExecutorJsonPojo;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<CommentJsonPojo> comments = new ArrayList<>();

    public TaskResponsePojo(
        Long id,
        String content,
        Status status,
        Priority priority,
        TaskExecutor taskExecutorJsonPojo,
        TaskCreator taskCreatorJsonPojo,
        List<CommentJsonPojo> comments
    ) {

        this.id = id;
        this.status = status;
        this.content = content;
        this.priority = priority;
        this.taskExecutorJsonPojo = taskExecutorJsonPojo;
        this.taskCreatorJsonPojo = taskCreatorJsonPojo;
        this.comments = comments;
    }

    @Override
    @JsonIgnore
    public String getExecutorUsername() {
        return MiscUtils.nullOrApply(taskExecutorJsonPojo, UserInfo::getUsername);
    }

    @Override
    @JsonIgnore
    public String getCreatorUsername() {
        return this.taskCreatorJsonPojo.getUsername();
    }

    @Nullable
    @Override
    public Long getTaskId() {
        return getId();
    }
}
