package org.effective_mobile.task_management_system.pojo.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class SigninResponse {
    @JsonProperty
    private String jwtToken;
}