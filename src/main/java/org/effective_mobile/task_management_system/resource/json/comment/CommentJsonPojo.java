package org.effective_mobile.task_management_system.resource.json.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.resource.json.JsonPojo;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
public class CommentJsonPojo implements JsonPojo {
    @JsonProperty private Long id;
    @JsonProperty private String content;
    @JsonProperty private Date creationDate;
    @JsonProperty private String username;
}
