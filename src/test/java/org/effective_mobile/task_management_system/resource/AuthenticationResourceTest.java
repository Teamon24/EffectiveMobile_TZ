package org.effective_mobile.task_management_system.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.effective_mobile.task_management_system.component.UsernameProvider;
import org.effective_mobile.task_management_system.confings.IntegrationTest;
import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.resource.json.UserCreationResponsePojo;
import org.effective_mobile.task_management_system.resource.json.auth.SigninRequestPojo;
import org.effective_mobile.task_management_system.resource.json.auth.SignupRequestPojo;
import org.effective_mobile.task_management_system.security.AuthTokenComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AuthenticationResourceTest extends IntegrationTest {

    @Autowired private ObjectMapper objectMapper;
    @Autowired private AuthTokenComponent authTokenComponent;
    @Autowired private UsernameProvider usernameProvider;

    /**
     * Test for {@link AuthenticationResource#signin} и {@link AuthenticationResource#signup}.
     */
    @Test
    public void signInSignUpTest() throws Exception {
        testSignUp();
        testSignIn();
    }

    private void testSignUp() throws Exception {
        SignupRequestPojo signupRequestPojo = new SignupRequestPojo(email, username, password);
        MvcResult mvcResult = post(Api.SIGN_UP, signupRequestPojo).andReturn();

        UserCreationResponsePojo userCreationResponsePojo = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserCreationResponsePojo.class);

        User user = userRepository.findOrThrow(User.class, userCreationResponsePojo.getId());

        Assertions.assertTrue(passwordEncoder.matches(password, user.getPassword()));
        Assertions.assertEquals(user.getEmail(), email);
        Assertions.assertEquals(user.getUsername(), username);
    }

    private void testSignIn() throws Exception {
        SigninRequestPojo signinRequestPojo = new SigninRequestPojo(email, password);
        MvcResult resultActions =
            post(Api.SIGN_IN, signinRequestPojo)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        final JsonNode node = objectMapper.readTree(resultActions.getResponse().getContentAsString());

        JsonNode authTokenJsonNode = node.get(authTokenNameLazy.get());
        Assertions.assertNotNull(authTokenJsonNode);
        String token = authTokenJsonNode.asText();
        Assertions.assertTrue(StringUtils.isNotBlank(token));

        Optional<User> optionalUser = userRepository.findByEmail(email);
        Assertions.assertTrue(optionalUser.isPresent());

        User user = optionalUser.get();
        String subject = authTokenComponent.validateTokenAndGetUsername(token);
        Assertions.assertEquals(usernameProvider.getUsername(user), subject);
        Assertions.assertTrue(passwordEncoder.matches(password, user.getPassword()));
    }

    private ResultActions post(String signin, Object body) throws Exception {
        return mvc
            .perform(MockMvcRequestBuilders
                .post(signin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asString(body))
            )
            .andExpect(status().isOk());
    }
}
