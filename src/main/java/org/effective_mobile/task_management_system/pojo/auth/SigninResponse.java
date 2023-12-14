package org.effective_mobile.task_management_system.pojo.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.resource.Api;

@AllArgsConstructor
@NoArgsConstructor
public class SigninResponse {

    @JsonProperty(Api.TOKEN_NAME)
    private String token;
}