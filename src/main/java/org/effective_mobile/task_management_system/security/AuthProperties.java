package org.effective_mobile.task_management_system.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.effective_mobile.task_management_system.pojo.TimeToLiveInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthProperties {

    public AuthProperties(
        ObjectMapper objectMapper,
        @Value("${app.auth.token.ttl}") String tokenTimeToLiveInfo,
        @Value("${app.auth.token.name}") String authTokenName,
        @Value("${app.auth.secret}") final String secret
    ) throws JsonProcessingException {
        this.tokenTimeToLiveInfo = objectMapper.readValue(tokenTimeToLiveInfo, TimeToLiveInfo.class);
        this.authTokenName = authTokenName;
        this.secret = secret;
    }

    public final TimeToLiveInfo tokenTimeToLiveInfo;
    public final String authTokenName;
    public final String secret;
}
