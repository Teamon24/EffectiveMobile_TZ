package org.effective_mobile.task_management_system.confings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import net.datafaker.Faker;
import org.effective_mobile.task_management_system.TaskManagementSystemApp;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.repository.TaskRepository;
import org.effective_mobile.task_management_system.repository.UserRepository;
import org.effective_mobile.task_management_system.security.CustomUserDetails;
import org.effective_mobile.task_management_system.security.JwtAuthTokenComponent;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.function.BiFunction;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = TaskManagementSystemApp.class)
@AutoConfigureMockMvc
public abstract class IntegrationTest {

    @Value("${app.auth.cookieName}")
    protected String cookieName;

    protected final String username = "teamon24";
    protected final String email = username + "@gmail.com";
    protected final String password = new Faker().internet().password(8, 20, true, true, true);
    protected final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Autowired
    protected MockMvc mvc;
    @Autowired protected TaskRepository taskRepository;
    @Autowired protected UserRepository userRepository;
    @Autowired protected ObjectMapper objectMapper;
    @Autowired private JwtAuthTokenComponent jwtTokenComponent;

    protected User user;

    protected MockHttpServletResponse postJson(User user, String path, Object payload) throws Exception {
        return methodJson(user, path, payload, MockMvcRequestBuilders::post).andReturn().getResponse();
    }

    protected MockHttpServletResponse putJson(User user, String path, Object payload) throws Exception {
        return methodJson(user, path, payload, MockMvcRequestBuilders::put).andReturn().getResponse();
    }

    protected MockHttpServletResponse getJson(User user, String path) throws Exception {
        return methodJson(user, path, null, MockMvcRequestBuilders::get).andReturn().getResponse();
    }

    protected MockHttpServletResponse deleteJson(User user, String path, Object payload) throws Exception {
        return methodJson(user, path, payload, MockMvcRequestBuilders::delete).andReturn().getResponse();
    }

    protected ResultActions methodJson(
        User user,
        String path,
        @Nullable Object payload,
        BiFunction<String, Object[], MockHttpServletRequestBuilder> method
    ) throws Exception {

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        return mvc.perform(
            method.apply(path, new Object[]{})
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload == null ? "" : asString(payload))
                .cookie(new Cookie(cookieName, jwtTokenComponent.generateTokenCookie(customUserDetails).getValue()))
        );
    }

    protected String asString(Object taskCreationPayload) throws JsonProcessingException {
        return objectMapper.writeValueAsString(taskCreationPayload);
    }

    protected <O> O asObject(String objectAsString, Class<O> objectClass) throws JsonProcessingException {
        return objectMapper.readValue(objectAsString, objectClass);
    }

    public User createUser(String username) {
        return User.builder()
            .username(username)
            .email(username + "@gmail.com")
            .password(passwordEncoder.encode(password))
            .build();
    }
}
