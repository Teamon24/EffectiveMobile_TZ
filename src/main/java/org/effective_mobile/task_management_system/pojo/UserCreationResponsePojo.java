package org.effective_mobile.task_management_system.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationResponsePojo {
    @Getter
    @JsonProperty private Long id;
    @JsonProperty private String username;
    @JsonProperty private String email;
}
