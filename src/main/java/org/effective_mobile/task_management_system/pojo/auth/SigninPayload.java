package org.effective_mobile.task_management_system.pojo.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SigninPayload {

    @Email
    @JsonProperty
    private String email;

    @JsonProperty
    private String password;
}
 