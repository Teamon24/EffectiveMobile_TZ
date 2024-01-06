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
import org.effective_mobile.task_management_system.component.UsernameProvider;
import org.effective_mobile.task_management_system.exception.auth.TokenAuthenticationException;
import org.effective_mobile.task_management_system.exception.messages.AuthExceptionMessages;
import org.effective_mobile.task_management_system.pojo.TimeToLiveInfo;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class JwtAuthTokenComponent implements AuthTokenComponent {

    private final UsernameProvider usernameProvider;
    private final AuthProperties authProperties;

    private final Algorithm hmac512;
    private final JWTVerifier verifier;

    public JwtAuthTokenComponent(
        UsernameProvider usernameProvider,
        AuthProperties authProperties
    ) {
        this.usernameProvider = usernameProvider;
        this.authProperties = authProperties;
        this.hmac512 = Algorithm.HMAC512(this.authProperties.secret);
        this.verifier = JWT.require(this.hmac512).build();
    }

    @Override
    public String getTokenFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, authProperties.authTokenName);
        return getToken(cookie);
    }

    @Override
    public @Nullable String getToken(Cookie cookie) {
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
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
        return ResponseCookie.from(authProperties.authTokenName, token).maxAge(24 * 60 * 60).httpOnly(true).build();
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
    public DecodedJWT validateToken(final String token) throws TokenAuthenticationException {
        if (token == null) {
            throw new TokenAuthenticationException(AuthExceptionMessages.noTokenInCookie(authProperties.authTokenName));
        }

        try {
            return verifier.verify(token);
        } catch (final JWTVerificationException verificationEx) {
            throw new TokenAuthenticationException(verificationEx);
        }
    }

    @Override
    public String validateTokenAndGetUsername(final String token) throws TokenAuthenticationException {
        return validateToken(token).getSubject();
    }

    private String generateToken(final String subject) {
        final Instant now = Instant.now();
        return JWT.create()
            .withSubject(subject)
            .withIssuer("app")
            .withIssuedAt(now)
            .withExpiresAt(getExpiresAt(now))
            .sign(this.hmac512);
    }

    private Instant getExpiresAt(Instant now) {
        long millis = toMillis(authProperties.tokenTimeToLiveInfo);
        return now.plusMillis(millis);
    }

    public static long toMillis(TimeToLiveInfo tokenTimeToLiveInfo) {
        TimeUnit tokenTimeUnit = tokenTimeToLiveInfo.getType();
        int timeToLive = tokenTimeToLiveInfo.getValue();
        return TimeUnit.MILLISECONDS.convert(timeToLive, tokenTimeUnit);
    }
}