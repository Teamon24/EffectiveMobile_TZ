package org.effective_mobile.task_management_system.resource.json.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.pojo.HasCreatorUsername;
import org.effective_mobile.task_management_system.pojo.HasExecutorUsername;
import org.effective_mobile.task_management_system.resource.json.CommentJsonPojo;
import org.effective_mobile.task_management_system.resource.json.JsonPojoId;

import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor
public class TaskResponsePojo extends TaskEssential implements JsonPojoId, HasExecutorUsername, HasCreatorUsername {

    @JsonProperty private Long id;
    @JsonProperty private String status;

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
        String status,
        String priority,
        TaskExecutor taskExecutorJsonPojo,
        TaskCreator taskCreatorJsonPojo,
        List<CommentJsonPojo> comments
    ) {
        super(priority, content);

        this.id = id;
        this.status = status;
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
