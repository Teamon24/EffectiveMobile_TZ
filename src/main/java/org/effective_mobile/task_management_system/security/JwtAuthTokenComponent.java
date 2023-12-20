package org.effective_mobile.task_management_system.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.effective_mobile.task_management_system.exception.InvalidAuthTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.time.Instant;

@Component
public class JwtAuthTokenComponent implements AuthTokenComponent {

    @Value("${app.auth.expirationMs}")
    private Long jwtTokenExpirationTimeMillis;

    @Value("${app.auth.cookieName}")
    private String tokenName;

    private final Algorithm hmac512;
    private final JWTVerifier verifier;

    public JwtAuthTokenComponent(@Value("${app.auth.secret}") final String secret) {
        this.hmac512 = Algorithm.HMAC512(secret);
        this.verifier = JWT.require(this.hmac512).build();
    }

    @Override
    public ResponseCookie getCleanTokenCookie() {
        return ResponseCookie.from(tokenName, "").path("/").maxAge(0).build();
    }

    @Override
    public String getTokenFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, tokenName);
        return getToken(cookie);
    }

    @Override
    public String getToken(Cookie cookie) {
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    @Override
    public ResponseCookie generateTokenCookie(UserDetails userDetails) {
        String token = generateToken(userDetails);
        return ResponseCookie.from(tokenName, token).maxAge(24 * 60 * 60).httpOnly(true).build();
    }

    @Override
    public String generateToken(final UserDetails userDetails) {
        final Instant now = Instant.now();
        return JWT.create()
            .withSubject(userDetails.getUsername())
            .withIssuer("app")
            .withIssuedAt(now)
            .withExpiresAt(now.plusMillis(jwtTokenExpirationTimeMillis))
            .sign(this.hmac512);
    }

    @Override
    public void validateToken(final String token) throws InvalidAuthTokenException {
        try {
            verifier.verify(token);
        } catch (final JWTVerificationException verificationEx) {
            throw new InvalidAuthTokenException(verificationEx);
        }
    }

    @Override
    public String validateTokenAndGetSubject(final String token) throws InvalidAuthTokenException {
        try {
            return verifier.verify(token).getSubject();
        } catch (final JWTVerificationException verificationEx) {
            throw new InvalidAuthTokenException(verificationEx);
        }
    }
}