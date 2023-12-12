package org.effective_mobile.task_management_system.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.effective_mobile.task_management_system.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.time.Instant;

@Service
public class JwtTokenComponent {

    @Value("${app.jwt.expirationMs}")
    private Long jwtTokenExpirationTimeMillis;

    private final Algorithm hmac512;
    private final JWTVerifier verifier;

    @Value("${app.jwt.cookieName}")
    private String jwtCookie;

    public JwtTokenComponent(@Value("${app.jwt.secret}") final String secret) {
        this.hmac512 = Algorithm.HMAC512(secret);
        this.verifier = JWT.require(this.hmac512).build();
    }

    public ResponseCookie getCleanTokenCookie() {
        return ResponseCookie.from(jwtCookie, "").path("/").maxAge(0).build();
    }

    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    public ResponseCookie generateJwtCookie(UserDetails userDetails) {
        String jwt = generateToken(userDetails);
        return ResponseCookie.from(jwtCookie, jwt).maxAge(24 * 60 * 60).httpOnly(true).build();
    }

    public String generateToken(final UserDetails userDetails) {
        final Instant now = Instant.now();
        return JWT.create()
            .withSubject(userDetails.getUsername())
            .withIssuer("app")
            .withIssuedAt(now)
            .withExpiresAt(now.plusMillis(jwtTokenExpirationTimeMillis))
            .sign(this.hmac512);
    }

    public String validateJwtToken(final String token) {
        try {
            return verifier.verify(token).getSubject();
        } catch (final JWTVerificationException verificationEx) {
            throw new InvalidTokenException(verificationEx);
        }
    }
}