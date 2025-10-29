package com.example.prm392_final_prj.utils;

import android.util.Base64;

import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class JwtUtils {

    private static final String SECRET = "ONLINE_TOUR_BOOKING_SKEY";
    public static String getRoleFromToken(String token) {
        if (token == null) return null;
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;
            String payload = parts[1];
            String json = decodeBase64UrlToString(payload);
            if(json == null) return null;
            JSONObject jsonObject = new JSONObject(json);
            if(jsonObject.has("role")) return jsonObject.getString("role");

        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private static String decodeBase64UrlToString(String payload) {
        try {
            // try URL_SAFE flags first (recommended for JWT)
            byte[] decoded = Base64.decode(payload, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
            return new String(decoded);
        } catch (IllegalArgumentException ex) {
            try {
                // fallback: normalize base64url -> base64 and decode with DEFAULT
                String normalized = payload.replace('-', '+').replace('_', '/');
                int pad = (4 - (normalized.length() % 4)) % 4;
                for (int i = 0; i < pad; i++) normalized += '=';
                byte[] decoded = Base64.decode(normalized, Base64.DEFAULT);
                return new String(decoded);
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    public static String createJWT(String email, String role) {
        try {
            JSONObject header = new JSONObject();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            JSONObject payload = new JSONObject();
            payload.put("email", email);
            payload.put("role", role);
            payload.put("iat", System.currentTimeMillis() / 1000);

            String headerBase64 = base64UrlEncode(header.toString().getBytes());
            String payloadBase64 = base64UrlEncode(payload.toString().getBytes());
            String signature = hmacSha256(headerBase64 + "." + payloadBase64, SECRET);

            return headerBase64 + "." + payloadBase64 + "." + signature;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean verifyJWT(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;
            String signature = hmacSha256(parts[0] + "." + parts[1], SECRET);
            return signature.equals(parts[2]);
        } catch (Exception e) {
            return false;
        }
    }

    public static JSONObject decodePayload(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;
            String json = new String(Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP));
            return new JSONObject(json);
        } catch (Exception e) {
            return null;
        }
    }

    private static String base64UrlEncode(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
    }

    private static String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
        byte[] hash = mac.doFinal(data.getBytes());
        return base64UrlEncode(hash);
    }
}
