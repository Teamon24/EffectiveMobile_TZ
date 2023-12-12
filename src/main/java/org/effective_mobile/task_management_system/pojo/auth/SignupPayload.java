package org.effective_mobile.task_management_system.pojo.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.validator.Signup;
import org.effective_mobile.task_management_system.validator.StrongPassword;

import static org.effective_mobile.task_management_system.validator.Signup.Type.EMAIL;
import static org.effective_mobile.task_management_system.validator.Signup.Type.USERNAME;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SignupPayload {

    @JsonProperty
    @Email
    @Signup(field = EMAIL)
    private String email;

    @JsonProperty
    @Signup(field = USERNAME)
    private String username;

    @JsonProperty
    @StrongPassword private String password;
}
