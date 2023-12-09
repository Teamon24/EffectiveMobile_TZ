package org.effective_mobile.task_management_system.pojo;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.effective_mobile.task_management_system.enums.Priority;
import org.effective_mobile.task_management_system.enums.Status;

import java.util.Date;

/**
 * Json pojo с информацией для создания задачи.
 */
@RequiredArgsConstructor
@Getter
@Setter
public class TaskCreation {

    @JsonProperty
    private Date creationDate;

    @JsonProperty
    private Status status;

    @JsonProperty
    private Priority priority;

    @JsonProperty
    private String content;

    @JsonProperty
    private String userName;

    @JsonCreator
    public TaskCreation(
        Date creationDate,
        Status status,
        Priority priority,
        String content,
        String userName
    ) {
        this.creationDate = creationDate;
        this.status = status;
        this.priority = priority;
        this.content = content;
        this.userName = userName;
    }
}

