package org.effective_mobile.task_management_system.security.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.primitives.Ints;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.effective_mobile.task_management_system.security.UsernameProvider;
import org.effective_mobile.task_management_system.exception.auth.TokenAuthenticationException;
import org.effective_mobile.task_management_system.exception.messages.AuthExceptionMessages;
import org.effective_mobile.task_management_system.pojo.TimeToLiveInfo;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtAuthTokenComponent implements AuthenticationTokenComponent {

    private final UsernameProvider usernameProvider;

    private final Algorithm hmac512;
    private final JWTVerifier verifier;
    private final String authTokenName;
    private final TimeToLiveInfo tokenTimeToLiveInfo;

    public JwtAuthTokenComponent(
        UsernameProvider usernameProvider,
        AuthenticationTokenProperties authenticationTokenProperties
    ) {
        this.usernameProvider = usernameProvider;
        this.authTokenName = authenticationTokenProperties.getAuthTokenName();
        this.tokenTimeToLiveInfo = authenticationTokenProperties.getTokenTimeToLiveInfo();

        this.hmac512 = Algorithm.HMAC512(authenticationTokenProperties.getSecret());
        this.verifier = JWT.require(this.hmac512).build();
    }

    @Override
    public String getTokenFromCookies(HttpServletRequest request) throws TokenAuthenticationException {
        Cookie cookie = WebUtils.getCookie(request, authTokenName);
        String token = getToken(cookie);
        if (token == null) {
            throw new TokenAuthenticationException(AuthExceptionMessages.noTokenInCookie(this.authTokenName));
        }
        return token;
    }

    @Override
    public boolean hasTokenInCookies(HttpServletRequest request) throws TokenAuthenticationException {
        Cookie authTokenCookie = WebUtils.getCookie(request, authTokenName);
        return getToken(authTokenCookie) != null;
    }

    @Override
    public @Nullable String getToken(Cookie cookie) {
        return cookie != null ? cookie.getValue() : null;
    }

    @Override
    public String generateToken(final UsernamePasswordAuthenticationToken token) {
        String subject = usernameProvider.getSubject(token);
        return generateToken(subject);
    }

    @Override
    public String generateToken(final UserDetails userDetails) {
        String subject = usernameProvider.getSubject(userDetails);
        return generateToken(subject);
    }

    @Override
    public ResponseCookie generateTokenResponseCookie(UserDetails userDetails) {
        String token = generateToken(userDetails);
        return ResponseCookie
            .from(authTokenName, token)
            .maxAge(secondsOf(tokenTimeToLiveInfo))
            .httpOnly(true)
            .build();
    }

    @Override
    public Cookie generateTokenCookie(UserDetails userDetails) {
        ResponseCookie tokenCookie = generateTokenResponseCookie(userDetails);
        Cookie cookie = new Cookie(tokenCookie.getName(), tokenCookie.getValue());
        cookie.setDomain(tokenCookie.getDomain());
        cookie.setPath(tokenCookie.getPath());
        cookie.setMaxAge(Ints.saturatedCast(tokenCookie.getMaxAge().toSeconds()));
        return cookie;
    }

    @Override
    public void validateToken(HttpServletRequest request) throws TokenAuthenticationException {
        String token = getTokenFromCookies(request);
        if (token == null) {
            throw new TokenAuthenticationException(AuthExceptionMessages.noTokenInCookie(this.authTokenName));
        }
        validateToken(token);
    }


    @Override
    public String validateTokenAndGetUsername(final String token) throws TokenAuthenticationException {
        return validateToken(token).getSubject();
    }

    @Override
    public Date getExpirationDate(final String token) throws TokenAuthenticationException {
        return validateToken(token).getExpiresAt();
    }

    @Override
    public Date getIssueDate(final String token) throws TokenAuthenticationException {
        return validateToken(token).getIssuedAt();
    }

    private String generateToken(final String subject) {
        final Instant now = Instant.now();
        return JWT.create()
            .withSubject(subject)
            .withIssuer("app")
            .withIssuedAt(now)
            .withExpiresAt(now.plusMillis(millisOf(this.tokenTimeToLiveInfo)))
            .sign(this.hmac512);
    }

    private DecodedJWT validateToken(final String token) throws TokenAuthenticationException {
        if (token == null) {
            throw new TokenAuthenticationException("Authentication token is null");
        }

        try {
            return verifier.verify(token);
        } catch (final JWTVerificationException verificationEx) {
            throw new TokenAuthenticationException(verificationEx);
        }
    }

    private long millisOf(TimeToLiveInfo tokenTimeToLiveInfo) {
        TimeUnit tokenTimeUnit = tokenTimeToLiveInfo.getType();
        int timeToLive = tokenTimeToLiveInfo.getValue();
        return TimeUnit.MILLISECONDS.convert(timeToLive, tokenTimeUnit);
    }

    private long secondsOf(TimeToLiveInfo tokenTimeToLiveInfo) {
        TimeUnit tokenTimeUnit = tokenTimeToLiveInfo.getType();
        int timeToLive = tokenTimeToLiveInfo.getValue();
        return TimeUnit.SECONDS.convert(timeToLive, tokenTimeUnit);
    }
}