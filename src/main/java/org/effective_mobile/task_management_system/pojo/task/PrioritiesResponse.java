package org.effective_mobile.task_management_system.pojo.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.enums.Priority;
import org.effective_mobile.task_management_system.pojo.ResponsePojo;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
public class PrioritiesResponse implements ResponsePojo {
    @Getter
    @JsonProperty
    private Set<Priority> priorities = Arrays.stream(Priority.values()).collect(Collectors.toSet());
}
