package org.effective_mobile.task_management_system.resource.json.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.effective_mobile.task_management_system.resource.json.RequestPojo;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SigninRequestPojo implements RequestPojo {

    @Email
    @JsonProperty
    private String email;

    @JsonProperty
    private String password;
}
 