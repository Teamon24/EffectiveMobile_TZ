package org.effective_mobile.task_management_system.resource.json.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.resource.json.ResponsePojo;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SigninResponsePojo implements ResponsePojo {

    @JsonProperty
    private String authToken;
}