package org.effective_mobile.task_management_system.maintain.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.effective_mobile.task_management_system.pojo.TimeToLiveInfo;

@Getter
@AllArgsConstructor
public class CacheSettings {
	@JsonProperty private String name;
	@JsonProperty private TimeToLiveInfo ttl;
	@JsonProperty private boolean enabled;
}