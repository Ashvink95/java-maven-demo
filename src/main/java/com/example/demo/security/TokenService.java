package com.example.demo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

/**
 * Issues and validates signed auth tokens without any external JWT library.
 *
 * A token is "{base64url(payload)}.{base64url(HMAC-SHA256(payload))}" where the
 * payload is "userId:email:expiresAtMillis". The signature is verified on every
 * protected request, so tokens cannot be forged without the secret.
 */
@Component
public class TokenService {

    private static final long EXPIRATION_MS = 24L * 60 * 60 * 1000; // 24 hours

    private final byte[] secret;

    public TokenService(
            @Value("${app.auth.secret:change-me-demo-secret-please-rotate-0123456789}") String secret) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    /** Creates a signed token for the given user. */
    public String generateToken(Long userId, String email) {
        long expiresAt = System.currentTimeMillis() + EXPIRATION_MS;
        String payload = userId + ":" + email + ":" + expiresAt;
        String encodedPayload = base64Url(payload.getBytes(StandardCharsets.UTF_8));
        return encodedPayload + "." + sign(encodedPayload);
    }

    /** Returns the user id if the token is valid and unexpired, else empty. */
    public Optional<Long> validate(String token) {
        try {
            if (token == null || token.isBlank()) {
                return Optional.empty();
            }
            int dot = token.indexOf('.');
            if (dot < 0) {
                return Optional.empty();
            }
            String encodedPayload = token.substring(0, dot);
            String signature = token.substring(dot + 1);
            if (!sign(encodedPayload).equals(signature)) {
                return Optional.empty();
            }
            String payload = new String(Base64.getUrlDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
            String[] parts = payload.split(":");
            if (parts.length != 3) {
                return Optional.empty();
            }
            long expiresAt = Long.parseLong(parts[2]);
            if (System.currentTimeMillis() > expiresAt) {
                return Optional.empty();
            }
            return Optional.of(Long.parseLong(parts[0]));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return base64Url(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to sign token", ex);
        }
    }

    private String base64Url(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }
}
