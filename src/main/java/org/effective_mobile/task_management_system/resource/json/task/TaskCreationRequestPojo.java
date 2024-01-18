package org.effective_mobile.task_management_system.resource.json.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.effective_mobile.task_management_system.resource.json.RequestPojo;

/**
 * Json pojo с информацией для создания задачи.
 */
@Getter
@Setter
@NoArgsConstructor
public class TaskCreationRequestPojo implements RequestPojo {

    public static final String CONTENT_FIELD_NAME = "content";
    public static final String PRIORITY_FIELD_NAME = "priority";

    @JsonProperty(PRIORITY_FIELD_NAME)
    private String priority;

    @JsonProperty(CONTENT_FIELD_NAME)
    private String content;

    @Builder
    private TaskCreationRequestPojo(String priority, String content) {
        this.priority = priority;
        this.content = content;
    }
}

