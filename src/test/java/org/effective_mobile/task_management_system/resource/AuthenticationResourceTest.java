package org.effective_mobile.task_management_system.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.effective_mobile.task_management_system.TaskManagementSystemApp;
import org.effective_mobile.task_management_system.entity.Role;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.pojo.SigninRequest;
import org.effective_mobile.task_management_system.pojo.SignupRequest;
import org.effective_mobile.task_management_system.repository.RoleRepository;
import org.effective_mobile.task_management_system.repository.UserRepository;
import org.effective_mobile.task_management_system.security.ApiPath;
import org.effective_mobile.task_management_system.security.AuthenticationResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.effective_mobile.task_management_system.enums.UserRole.USER;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = TaskManagementSystemApp.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AuthenticationResourceTest {

    private final String username = "teamon24";
    private final String email = username + "@gmail.com";
    private final String password = "12345";

    @Value("${app.jwt.cookieName}")
    private String jwtCookieName;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    /**
     * Test for {@link AuthenticationResource#signin} и {@link AuthenticationResource#signup}.
     */
    @Test
    public void signinSignUpTest() throws Exception {
        Role role = new Role(USER);
        roleRepository.saveAndFlush(role);
        testSignUp();
        Cookie jwtTokenCookie = testSignIn();
    }

    private void testSignUp() throws Exception {
        SignupRequest signupRequest = new SignupRequest(email, username, password);
        MvcResult mvcResult = post(ApiPath.signup, signupRequest).andReturn();
        Assertions.assertEquals(1, Integer.valueOf(mvcResult.getResponse().getContentAsString()));

        User user = userRepository.findOrThrow(User.class, 1L);
        Assertions.assertTrue(passwordEncoder.matches(password, user.getPassword()));
        Assertions.assertEquals(user.getEmail(), email);
        Assertions.assertEquals(user.getUsername(), username);
    }

    private Cookie testSignIn() throws Exception {
        SigninRequest signinRequest = new SigninRequest(email, password);
        MvcResult resultActions =
            post(ApiPath.signin, signinRequest)
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

    private String asString(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }
}
