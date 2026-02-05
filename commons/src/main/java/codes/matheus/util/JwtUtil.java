package codes.matheus.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class JwtUtil {
    private final static @NotNull String secretKey = "rSIrNqwuo+rzHIIuIrc6USFH+YcsnZGYSaUhGK/CXYAx+XeV/6+oJ11KaKsXBDtm";
    private final static @NotNull String algorithm = "HS256";

    private JwtUtil() {
        throw new UnsupportedOperationException("the class should not be instantiated");
    }

    public static @NotNull String generateToken(@NotNull String subject, long expire) {
        long issuedAt = System.currentTimeMillis();
        long expiresAt = issuedAt + expire;

        @NotNull JsonObject object = new JsonObject();
        object.addProperty("sub", subject);
        object.addProperty("iat", issuedAt / 1000);
        object.addProperty("exp", expiresAt / 1000);

        @NotNull String header = "{\"alg\":\"" + algorithm + "\",\"typ\":\"JWT\"}";
        @NotNull String payload = object.toString();
        @NotNull String encodedHeader = base64UrlEncode(header.getBytes(StandardCharsets.UTF_8));
        @NotNull String encodedPayload = base64UrlEncode(payload.getBytes(StandardCharsets.UTF_8));
        @NotNull String signature = base64UrlEncode(hmacSha256(encodedHeader + "." + encodedPayload, secretKey));

        return encodedHeader + "." + encodedPayload + "." + signature;
    }

    public static @Nullable String validate(@Nullable String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        @NotNull String token = authHeader.substring(7).trim();
        @NotNull String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return null;
        }

        @NotNull String header = parts[0];
        @NotNull String payload = parts[1];
        @NotNull String signature = parts[2];

        @NotNull String expectedSignature = base64UrlEncode(hmacSha256(header + "." + payload, secretKey));
        if (!signature.equals(expectedSignature)) {
            return null;
        }

        @NotNull String payloadJson;
        try {
            payloadJson = new String(base64UrlDecode(payload), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }

        @NotNull JsonObject claims;
        try {
            claims = JsonParser.parseString(payloadJson).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }

        if (claims.has("exp")) {
            long exp = claims.get("exp").getAsLong() * 1000;
            if (System.currentTimeMillis() > exp) {
                return null;
            }
        }
        return claims.has("sub") ? claims.get("sub").getAsString() : null;
    }

    private static byte[] hmacSha256(@NotNull String data, @NotNull String secret) {
        try {
            @NotNull Mac mac = Mac.getInstance("HmacSHA256");
            @NotNull SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(key);
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed on generate signature HMAC-SHA256", e);
        }
    }

    private static @NotNull String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static byte[] base64UrlDecode(@NotNull String str) {
        return Base64.getUrlDecoder().decode(str);
    }
}
