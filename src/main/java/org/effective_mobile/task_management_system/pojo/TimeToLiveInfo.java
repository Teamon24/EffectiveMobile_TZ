package org.effective_mobile.task_management_system.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.effective_mobile.task_management_system.maintain.cache.TimeToLiveInfoDeserializer;

import java.util.concurrent.TimeUnit;

@Getter
@AllArgsConstructor
@JsonDeserialize(using = TimeToLiveInfoDeserializer.class)
public class TimeToLiveInfo {
    @JsonProperty private TimeUnit type;
    @JsonProperty private int value;
}
