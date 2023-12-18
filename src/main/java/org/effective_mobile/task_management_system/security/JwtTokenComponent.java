package org.effective_mobile.task_management_system.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.effective_mobile.task_management_system.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.time.Instant;

import static org.effective_mobile.task_management_system.resource.Api.TOKEN_NAME;

@Component
public class JwtTokenComponent implements TokenComponent {

    @Value("${app.jwt.expirationMs}")
    private Long jwtTokenExpirationTimeMillis;

    private final Algorithm hmac512;
    private final JWTVerifier verifier;

    public JwtTokenComponent(@Value("${app.jwt.secret}") final String secret) {
        this.hmac512 = Algorithm.HMAC512(secret);
        this.verifier = JWT.require(this.hmac512).build();
    }

    @Override
    public ResponseCookie getCleanTokenCookie() {
        return ResponseCookie.from(TOKEN_NAME, "").path("/").maxAge(0).build();
    }

    @Override
    public String getTokenFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, TOKEN_NAME);
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
        return ResponseCookie.from(TOKEN_NAME, token).maxAge(24 * 60 * 60).httpOnly(true).build();
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
    public String validateToken(final String token) throws InvalidTokenException {
        try {
            return verifier.verify(token).getSubject();
        } catch (final JWTVerificationException verificationEx) {
            throw new InvalidTokenException(verificationEx);
        }
    }
}