package org.effective_mobile.task_management_system.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.effective_mobile.task_management_system.IntegrationTest;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.pojo.auth.SigninPayload;
import org.effective_mobile.task_management_system.pojo.auth.SignupPayload;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AuthenticationResourceTest extends IntegrationTest {

    @Value("${app.jwt.cookieName}")
    private String jwtCookieName;

    /**
     * Test for {@link AuthenticationResource#signin} и {@link AuthenticationResource#signup}.
     */
    @Test
    public void signInSignUpTest() throws Exception {
        testSignUp();
        Cookie jwtTokenCookie = testSignIn();
    }

    private void testSignUp() throws Exception {
        SignupPayload signupPayload = new SignupPayload(email, username, password);
        MvcResult mvcResult = post(Api.SIGN_UP, signupPayload).andReturn();
        Assertions.assertEquals(1, Integer.valueOf(mvcResult.getResponse().getContentAsString()));

        User user = userRepository.findOrThrow(User.class, 1L);
        Assertions.assertTrue(passwordEncoder.matches(password, user.getPassword()));
        Assertions.assertEquals(user.getEmail(), email);
        Assertions.assertEquals(user.getUsername(), username);
    }

    private Cookie testSignIn() throws Exception {
        SigninPayload signinPayload = new SigninPayload(email, password);
        MvcResult resultActions =
            post(Api.SIGN_IN, signinPayload)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        final JsonNode node = new ObjectMapper().readTree(resultActions.getResponse().getContentAsString());
        String accessToken = "accessToken";
        Assertions.assertTrue(node.has(accessToken));
        Cookie cookie = resultActions.getResponse().getCookie(jwtCookieName);
        Assertions.assertNotNull(cookie);
        return cookie;
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
