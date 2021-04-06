package net.thiccaxe.resonance.auth;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class TokenManager {
    private static final String authTokenChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String tokenChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";

    private final HashMap<UUID, Token> uuidTokenHashMap = new HashMap<>();






    public static String generateRandomToken(boolean authToken) {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        final StringBuilder token = new StringBuilder();
        final String chars = authToken ? authTokenChars : tokenChars;

        for (int i = 0; i < (authToken ? 8 : 64); i++) {
            token.append(chars.charAt(random.nextInt(0, chars.length())));
        }
        return token.toString();
    }
    public static String generateRandomToken() {
        return generateRandomToken(false);
    }
}

