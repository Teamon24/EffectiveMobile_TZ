package org.effective_mobile.task_management_system.pojo.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.pojo.ResponsePojo;
import org.effective_mobile.task_management_system.resource.Api;

@AllArgsConstructor
@NoArgsConstructor
public class SigninResponsePojo implements ResponsePojo {

    @JsonProperty
    private String authToken;
}