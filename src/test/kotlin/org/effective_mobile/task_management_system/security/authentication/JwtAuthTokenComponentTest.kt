package org.effective_mobile.task_management_system.security.authentication

import com.fasterxml.jackson.databind.ObjectMapper
import home.dsl.JUnit5ArgumentsDsl.args
import home.dsl.JUnit5ArgumentsDsl.stream
import org.effective_mobile.task_management_system.RandomUsers
import org.effective_mobile.task_management_system.pojo.TimeToLiveInfo
import org.effective_mobile.task_management_system.security.EmailAsUsernameProvider
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.stream.Stream

/**
 * Test for class [JwtAuthTokenComponent].
 */
@RunWith(MockitoJUnitRunner::class)
class JwtAuthTokenComponentTest {

    private val usernameProvider = mock(EmailAsUsernameProvider::class.java)
    private val authTokenName = "auth_token"

    private val authProperties = spy(
        AuthProperties(
            ObjectMapper(), "{\"${TimeUnit.MINUTES.name}\":5}", authTokenName, "vaeOk2Mx4p9COZfRRbVsVxC7EuV9TW18pWf2"
        )
    )

    private var component: JwtAuthTokenComponent

    private val email = RandomUsers.user().getEmail()
    private val userDetails = spy(UserDetails::class.java)

    init {
        Mockito.doReturn(email).`when`(userDetails).username
        Mockito.`when`(usernameProvider.getSubject(ArgumentMatchers.eq(userDetails))).thenReturn(email)
        component = JwtAuthTokenComponent(usernameProvider, authProperties)
    }

    /**
     * Test for [JwtAuthTokenComponent.generateToken].
     */
    @Test
    fun generateTokenTest() {
        val authenticationToken = component.generateToken(userDetails)
        Assertions.assertEquals(email, component.validateTokenAndGetUsername(authenticationToken))
    }

    /**
     * Test for [JwtAuthTokenComponent.getExpirationDate].
     */
    @ParameterizedTest
    @MethodSource("getExpirationDateTestData")
    fun getExpirationDateTest(timeToLiveInfo: TimeToLiveInfo) {
        authProperties.also {
            Mockito.`when`(it.tokenTimeToLiveInfo).thenReturn(timeToLiveInfo)
            component = JwtAuthTokenComponent(usernameProvider, it)
        }

        val authenticationToken = component.generateToken(userDetails)
        val expirationDate = component.getExpirationDate(authenticationToken)
        val issueDate = component.getIssueDate(authenticationToken)

        val actual = expirationDate - issueDate

        val expected = TimeUnit.MILLISECONDS.convert(
            authProperties.tokenTimeToLiveInfo.value.toLong(),
            authProperties.tokenTimeToLiveInfo.type
        )

        Assertions.assertEquals(expected, actual)
    }

    /**
     * Test for [JwtAuthTokenComponent.generateTokenCookie].
     */
    @Test
    fun generateTokenCookieTest() {
        val authTokenCookie = component.generateTokenCookie(userDetails)
        authProperties.tokenTimeToLiveInfo.apply {
            Assertions.assertEquals(
                TimeUnit.SECONDS.convert(value.toLong(), type).toInt(),
                authTokenCookie.maxAge
            )
        }
    }

    private operator fun Date.minus(date: Date) = toInstant().toEpochMilli() - date.toInstant().toEpochMilli()

    companion object {
        @JvmStatic
        fun getExpirationDateTestData(): Stream<Arguments> {
            return stream {
                args { +TimeToLiveInfo(TimeUnit.MINUTES, 5) }
                args { +TimeToLiveInfo(TimeUnit.HOURS, 5) }
                args { +TimeToLiveInfo(TimeUnit.DAYS, 1) }
            }
        }
    }
}
