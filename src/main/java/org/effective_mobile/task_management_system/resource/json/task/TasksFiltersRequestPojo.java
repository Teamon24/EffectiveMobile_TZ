package org.effective_mobile.task_management_system.resource.json.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.component.validator.NullOrNotBlank;

@NoArgsConstructor
@Getter
public
class TasksFiltersRequestPojo {

    @JsonProperty
    @NullOrNotBlank
    private String creatorUsername;

    @JsonProperty
    @NullOrNotBlank
    private String executorUsername;

    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty
    private Boolean withComments = false;
}
