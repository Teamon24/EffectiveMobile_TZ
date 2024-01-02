package org.effective_mobile.task_management_system.resource.json.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class DeletedTaskResponsePojo {
    @JsonProperty private Long id;
}
