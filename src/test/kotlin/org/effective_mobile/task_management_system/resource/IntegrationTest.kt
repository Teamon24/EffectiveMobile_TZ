package org.effective_mobile.task_management_system.resource

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.Nullable
import org.effective_mobile.task_management_system.TaskManagementSystemApp
import org.effective_mobile.task_management_system.security.UsernameProvider
import org.effective_mobile.task_management_system.database.entity.User
import org.effective_mobile.task_management_system.exception.ErrorInfo
import org.effective_mobile.task_management_system.exception.ValidationErrorInfo
import org.effective_mobile.task_management_system.exception.auth.TokenAuthenticationException
import org.effective_mobile.task_management_system.exception.messages.AuthExceptionMessages
import org.effective_mobile.task_management_system.resource.json.RequestPojo
import org.effective_mobile.task_management_system.security.CustomUserDetails
import org.effective_mobile.task_management_system.security.authentication.AuthProperties
import org.effective_mobile.task_management_system.security.authentication.JwtAuthTokenComponent
import org.effective_mobile.task_management_system.security.authorization.AuthorizationComponent
import org.effective_mobile.task_management_system.security.authorization.RequiredAuthorizationInfo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.*
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.web.bind.MethodArgumentNotValidException
import kotlin.reflect.KClass

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = [TaskManagementSystemApp::class])
@AutoConfigureMockMvc
@TestPropertySource(properties = ["spring.jpa.hibernate.ddl-auto=create-drop"])
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
abstract class IntegrationTest {

    protected class HttpRequestInfo {
        var body: RequestPojo? = null
        lateinit var method: HttpMethod
        lateinit var url: String
        lateinit var response: Class<*>
        lateinit var responseBody: MockHttpServletResponse
    }

    private var _customUserDetails: CustomUserDetails? = null
    protected var customUserDetails: CustomUserDetails
        get() {
            return _customUserDetails!!
        } set(value) {
            _customUserDetails = value
        }

    @Autowired protected lateinit var mvc: MockMvc
    @Autowired protected lateinit var objectMapper: ObjectMapper

    @Autowired protected lateinit var authProperties: AuthProperties
    @Autowired protected lateinit var jwtTokenComponent: JwtAuthTokenComponent
    @Autowired protected lateinit var authorizationComponent: AuthorizationComponent
    @Autowired protected lateinit var usernameProvider: UsernameProvider

    @BeforeEach
    fun setUp() {
        _customUserDetails = null
    }

    private val post = { urlTemplate: String -> post(urlTemplate)}
    private val put = { urlTemplate: String -> put(urlTemplate)}
    private val get = { urlTemplate: String -> get(urlTemplate)}
    private val delete = { urlTemplate: String -> delete(urlTemplate)}

    private fun User.perform(
        mockMvc: MockMvc,
        path: String,
        @Nullable payload: RequestPojo?,
        method: (String) -> MockHttpServletRequestBuilder
    ): MvcResult {
        val requestBuilder = method(path)
        requestBuilder.apply {
            payload?.let {
                contentType(MediaType.APPLICATION_JSON)
                content(objectMapper.writeValueAsString(it))
            }

            _customUserDetails?.let {
                cookie(jwtTokenComponent.generateTokenCookie(it));
            }
        }
        return mockMvc.perform(requestBuilder).andReturn()
    }

    class RequiresAuthorizationInfoImplTest(val user: User):
        RequiredAuthorizationInfo {
        override fun getUserId() = user.id!!
    }

    protected fun User.authenticated() {
        _customUserDetails = CustomUserDetails(
            this,
            usernameProvider.getUsername(this),
            authorizationComponent.getAuthorities(RequiresAuthorizationInfoImplTest(this))
        )
    }

    protected fun User.unauthorized() {
        _customUserDetails = CustomUserDetails(
            this,
            usernameProvider.getUsername(this),
            hashSetOf()
        )
    }



    protected inline fun <reified T> MockHttpServletResponse.getBody(): T =
        objectMapper.readValue(contentAsString, T::class.java)

    protected operator fun <T> User.invoke(block: User.() -> T): T = this.block()

    protected fun User.send(mvc: MockMvc, create: HttpRequestInfo.() -> Unit): HttpRequestInfo {
        return HttpRequestInfo().apply(create).apply {
            when (method) {
                GET    -> perform(mvc, url, body         , get)    .response.also { responseBody = it }
                DELETE -> perform(mvc, url, body         , delete) .response.also { responseBody = it }
                PUT    -> perform(mvc, url, body         , put)    .response.also { responseBody = it }
                POST   -> perform(mvc, url, bodyOrThrow(), post)   .response.also { responseBody = it }
                else -> throw UnsupportedOperationException("$method")
            }
        }
    }

    protected infix fun <T> HttpRequestInfo.response(block: MockHttpServletResponse.(HttpRequestInfo) -> T): T {
        return this.responseBody.block(this)
    }

    private fun HttpRequestInfo.bodyOrThrow() = body ?: throw absentRequestBodyEx(method)

    private fun absentRequestBodyEx(method: HttpMethod) = RuntimeException("$method has no request body")

    protected fun MockHttpServletResponse.assert401(requestInfo: HttpRequestInfo) {
        assertAny400(
            HttpStatus.UNAUTHORIZED,
            requestInfo,
            AuthExceptionMessages.noTokenInCookie(authProperties.authTokenName),
            TokenAuthenticationException::class.canonicalName
        )
    }

    protected fun MockHttpServletResponse.assert403(
        requestInfo: HttpRequestInfo, message: String, exceptionCanonicalName: String
    ) {
        assertAny400(HttpStatus.FORBIDDEN, requestInfo, message, exceptionCanonicalName)
    }

    protected fun MockHttpServletResponse.assert404(
        requestInfo: HttpRequestInfo, message: String, exceptionCanonicalName: String
    ) {
        assertAny400(HttpStatus.NOT_FOUND, requestInfo, message, exceptionCanonicalName)
    }

    protected fun MockHttpServletResponse.assert400(
        requestInfo: HttpRequestInfo, message: String, exceptionCanonicalName: String
    ) {
        assertAny400(HttpStatus.BAD_REQUEST, requestInfo, message, exceptionCanonicalName)
    }

    protected fun MockHttpServletResponse.assertAny400(
        httpStatus: HttpStatus,
        requestInfo: HttpRequestInfo,
        message: String,
        exceptionCanonicalName: String
    ) {
        val errorInfo = getBody<ErrorInfo>()
        Assertions.assertEquals(httpStatus.value(), errorInfo.status)
        Assertions.assertEquals(httpStatus.reasonPhrase, errorInfo.error)
        Assertions.assertEquals(requestInfo.url, errorInfo.path)

        Assertions.assertEquals(exceptionCanonicalName, errorInfo.exception)
        Assertions.assertEquals(message, errorInfo.message)
    }

    protected fun MockHttpServletResponse.assertValidationErrorInfo(requestInfo: HttpRequestInfo): ValidationErrorInfo {
        val validationErrorInfo = getBody<ValidationErrorInfo>()
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), validationErrorInfo.status)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.reasonPhrase, validationErrorInfo.error)
        Assertions.assertEquals(requestInfo.url, validationErrorInfo.path)
        Assertions.assertEquals(MethodArgumentNotValidException::class.canonicalName, validationErrorInfo.exception)
        Assertions.assertEquals(null, validationErrorInfo.message)
        return validationErrorInfo
    }

    protected inline fun ValidationErrorInfo.assertValidationError(
        errorIndex: Int,
        function: ValidationErrorInfo.ValidationError.() -> Unit
    ) {
        val expected = ValidationErrorInfo.ValidationError().apply(function)
        val actual = this.errors[errorIndex]
        Assertions.assertEquals(expected.field, actual.field)
        Assertions.assertEquals(expected.rejectedValue, actual.rejectedValue)
        Assertions.assertEquals(expected.message, actual.message)
        Assertions.assertEquals(expected.`object`, actual.`object`)
    }


    protected inline val KClass<*>.canonicalName: String get() = this.java.canonicalName
}