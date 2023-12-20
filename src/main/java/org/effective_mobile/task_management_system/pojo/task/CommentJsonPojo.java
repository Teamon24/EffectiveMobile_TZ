package org.effective_mobile.task_management_system.pojo.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
public class CommentJsonPojo {
    @JsonProperty private Long id;
    @JsonProperty private String content;
    @JsonProperty private Date creationDate;
    @JsonProperty private String username;
}
