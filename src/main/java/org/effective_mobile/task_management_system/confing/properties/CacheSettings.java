package org.effective_mobile.task_management_system.confing.properties;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.effective_mobile.task_management_system.utils.TimeToLiveInfoDeserializer;

import java.util.concurrent.TimeUnit;

@Getter
@AllArgsConstructor
public class CacheSettings {
	@JsonProperty private String name;
	@JsonProperty private TimeToLiveInfo ttl;
	@JsonProperty private boolean enabled;

	@JsonDeserialize(using = TimeToLiveInfoDeserializer.class)
	@AllArgsConstructor
	@Getter
	public static class TimeToLiveInfo {
		@JsonProperty private TimeUnit type;
		@JsonProperty private int value;
	}
}