package org.effective_mobile.task_management_system.resource.json.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.resource.json.JsonPojoId;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponsePojo implements JsonPojoId {
    @Getter
    @JsonProperty private Long id;
    @JsonProperty private String username;
    @JsonProperty private String email;
}
