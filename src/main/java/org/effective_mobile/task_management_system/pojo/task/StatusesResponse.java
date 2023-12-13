package org.effective_mobile.task_management_system.pojo.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.enums.Status;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
public class StatusesResponse {
    @Getter
    @JsonProperty
    private Set<Status> statuses = Arrays.stream(Status.values()).collect(Collectors.toSet());
}
