package org.effective_mobile.task_management_system.resource

import org.effective_mobile.task_management_system.AssertionsUtils
import org.effective_mobile.task_management_system.RandomUsers
import org.effective_mobile.task_management_system.database.entity.User
import org.effective_mobile.task_management_system.pojo.HasUserInfo
import org.effective_mobile.task_management_system.resource.json.assignment.SignupResponsePojo
import org.effective_mobile.task_management_system.resource.json.auth.SigninRequestPojo
import org.effective_mobile.task_management_system.resource.json.auth.SigninResponsePojo
import org.effective_mobile.task_management_system.resource.json.auth.SignupRequestPojo
import org.effective_mobile.task_management_system.security.authentication.AuthTokenComponent
import org.effective_mobile.task_management_system.utils.Api
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.platform.commons.util.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod.POST
import org.springframework.security.crypto.password.PasswordEncoder

class AuthenticationResourceTest : UserAndTaskIntegrationBase() {
    @Autowired
    private lateinit var authTokenComponent: AuthTokenComponent

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    /**
     * Test for [AuthenticationResource.signup].
     */
    @Test
    @Throws(Exception::class)
    fun testSignUp() {
        val user = RandomUsers.user()

        user {
            send(mvc) {
                method = POST
                url = Api.SIGN_UP
                body = SignupRequestPojo(getEmail(), getUsername(), getPassword())
            } response {
                val signupResponsePojo = getBody<SignupResponsePojo>()
                userRepository.findOrThrow(User::class.java, signupResponsePojo.id).let { foundUser ->
                    Assertions.assertTrue(passwordEncoder.matches(getPassword(), foundUser.getPassword()))

                    AssertionsUtils.assertEquals(user, foundUser, HasUserInfo::getEmail)
                    AssertionsUtils.assertEquals(user, foundUser, HasUserInfo::getUsername)
                }
            }
        }
    }

    /**
     * Test for [AuthenticationResource.signin].
     */
    @Test
    @Throws(Exception::class)
    fun testSignIn() {
        val notEncodedPassword = RandomUsers.safePassword()
        val user = saveAndFlush(RandomUsers.user { passwordEncoder.encode(notEncodedPassword) })

        user {
            send(mvc) {
                method = POST
                url = Api.SIGN_IN
                body = SigninRequestPojo(getEmail(), notEncodedPassword)
            } response {
                val authToken = getBody<SigninResponsePojo>().authToken
                Assertions.assertNotNull(authToken)
                Assertions.assertTrue(StringUtils.isNotBlank(authToken))

                userRepository.findByEmail(getEmail()).apply {
                    val foundUser = get()
                    Assertions.assertNotNull(foundUser)
                    val username = authTokenComponent.validateTokenAndGetUsername(authToken)
                    Assertions.assertEquals(usernameProvider.getUsername(foundUser), username)
                    Assertions.assertTrue(passwordEncoder.matches(notEncodedPassword, foundUser.getPassword()))
                }
            }
        }
    }
}
