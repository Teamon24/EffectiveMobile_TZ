package org.effective_mobile.task_management_system.resource.json.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.resource.json.RequestPojo;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentRequestPojo implements RequestPojo {
    @NotNull
    @JsonProperty private String executorUsername;
}
