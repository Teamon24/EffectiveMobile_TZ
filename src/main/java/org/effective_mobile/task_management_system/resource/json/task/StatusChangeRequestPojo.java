package org.effective_mobile.task_management_system.resource.json.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.resource.json.RequestPojo;

@NoArgsConstructor
@AllArgsConstructor
public class StatusChangeRequestPojo implements RequestPojo {

    public static final String STATUS_FIELD_NAME = "status";

    @Getter
    @JsonProperty(STATUS_FIELD_NAME)
    private String status;
}
