package org.effective_mobile.task_management_system.pojo.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.enums.Priority;
import org.effective_mobile.task_management_system.enums.Status;
import org.effective_mobile.task_management_system.pojo.CommentJsonPojo;
import org.effective_mobile.task_management_system.pojo.HasExecutorUsername;

import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor
public class TaskJsonPojo extends TaskEssential implements HasExecutorUsername {

    @JsonProperty private Long id;

    @JsonProperty("creator")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TaskCreatorJsonPojo taskCreatorJsonPojo;

    @JsonProperty("executor")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TaskExecutorJsonPojo taskExecutorJsonPojo;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<CommentJsonPojo> comments = new ArrayList<>();

    public TaskJsonPojo(
        Long id,
        String content,
        String status,
        String priority,
        TaskExecutorJsonPojo taskExecutorJsonPojo,
        TaskCreatorJsonPojo taskCreatorJsonPojo,
        List<CommentJsonPojo> comments
    ) {
        super(status, priority, content);
        this.id = id;
        this.taskExecutorJsonPojo = taskExecutorJsonPojo;
        this.taskCreatorJsonPojo = taskCreatorJsonPojo;
        this.comments = comments;
    }

    @Override
    @JsonIgnore
    public String getExecutorUsername() {
        return this.taskExecutorJsonPojo.getUsername();
    }

    @Override
    @JsonIgnore
    public String getCreatorUsername() {
        return this.taskCreatorJsonPojo.getUsername();
    }
}
