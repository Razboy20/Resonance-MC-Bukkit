package dev.razboy.resonance.manager;

import dev.razboy.resonance.Resonance;
import dev.razboy.resonance.config.ConfigType;
import dev.razboy.resonance.config.impl.DefaultConfig;
import dev.razboy.resonance.config.impl.TokensConfig;
import dev.razboy.resonance.token.AuthToken;
import dev.razboy.resonance.token.Token;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class TokenManager {
    private static final String TOKEN_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    //Auth tokens are used to connect a Player's minecraft account to a website session.
    //After validating an auth token, they are deleted and a Token is used for further authentication from the WebSocket server and a client.
    //Additionally, if a new AuthToken is created, a Token linked with the same player UUID is deleted, and therefore invalid.
    //Also, if a AuthToken expires after 5 minutes, it's linked Token also is deleted. Note that Tokens are not valid until an AuthToken is used, validated, and deleted.



    //Auth tokens. Linked with a regular token from creation
    //AuthToken object contains a Token object inside.
    private final HashMap<String, AuthToken> uuidToAuthTokens = new HashMap<>();
    private final HashMap<String, String> authTokenStringToUuids = new HashMap<>();
    //Tokens
    private final HashMap<String, Token> uuidToTokens = new HashMap<>();
    private final HashMap<String, String> tokenStringToUuids = new HashMap<>();

    public static final String DEV_TOKEN = "ABCDEF";
    public static final int TOKEN_LENGTH = 64;

    public TokenManager() {
    }

    public void generateAuthToken(String uuid, String username) {
        // If a AuthToken already exists with the given UUID linked with it, delete/replace it, regardless of age.
        if (uuidToAuthTokens.containsKey(uuid)) {
            authTokenStringToUuids.remove(uuidToAuthTokens.get(uuid).toString());
            uuidToAuthTokens.remove(uuid);
        }
        AuthToken authToken = new AuthToken(uuid, username);
        uuidToAuthTokens.put(uuid, authToken);
        authTokenStringToUuids.put(authToken.toString(), uuid);
        System.out.println("Table:");
        uuidToAuthTokens.forEach((key, value) -> System.out.println(key + " | " + value));
        System.out.println("Reversed Table:");
        authTokenStringToUuids.forEach((key, value) -> System.out.println(key + " | " + value));


    }
    public void generateAuthToken(String uuid, String username, String tokenString) {
        // If a AuthToken already exists with the given UUID linked with it, delete/replace it, regardless of age.
        if (uuidToAuthTokens.containsKey(uuid)) {
            authTokenStringToUuids.remove(uuidToAuthTokens.get(uuid).toString());
            uuidToAuthTokens.remove(uuid);
        }
        AuthToken authToken = new AuthToken(uuid, username, tokenString);
        uuidToAuthTokens.put(uuid, authToken);
        authTokenStringToUuids.put(authToken.toString(), uuid);
        System.out.println("Table:");
        uuidToAuthTokens.forEach((key, value) -> System.out.println(key + " | " + value));
        System.out.println("Reversed Table:");
        authTokenStringToUuids.forEach((key, value) -> System.out.println(key + " | " + value));
    }
    public AuthToken getAuthToken(String uuid) {
        if (uuidToAuthTokens.containsKey(uuid)) {
            return uuidToAuthTokens.get(uuid);
        }
        return null;
    }

    public AuthToken getAuthTokenFromAuthTokenString(String authToken) {
        // Return the AuthToken currently stored, regardless of whether it is expired. if it does not exist, return null.
        if (authTokenStringToUuids.containsKey(authToken)) {
            return uuidToAuthTokens.get(authTokenStringToUuids.get(authToken));
        }
        return null;

    }
    public boolean validateAuthToken(String authToken) {
        System.out.println("RAN");
        boolean toReturn = false;
        // Return if the provided auth token is valid. If the stored auth token has expired, remove it and return false.
        if (authTokenStringToUuids.containsKey(authToken)) {
            //if (uuidToAuthTokens.get(authTokenStringToUuids.get(authToken)).expired(60000L)) {
            System.out.println(uuidToAuthTokens.get(authTokenStringToUuids.get(authToken)).expired(20000L));
            if (!authToken.equals(DEV_TOKEN) && uuidToAuthTokens.get(authTokenStringToUuids.get(authToken)).expired(20000L)) {
                uuidToAuthTokens.remove(authTokenStringToUuids.get(authToken));
                authTokenStringToUuids.remove(authToken);
            } else {
                toReturn = true;
            }
        }
        System.out.println("Table:");
        uuidToAuthTokens.forEach((key, value) -> System.out.println(key + " | " + value));
        System.out.println("Reversed Table:");
        authTokenStringToUuids.forEach((key, value) -> System.out.println(key + " | " + value));
        return toReturn;
    }

    public Token getTokenFromAuthToken(String authToken) {
        //Return linked token of authtoken. If none exist, return null
        if (authTokenStringToUuids.containsKey(authToken)) {
            return uuidToAuthTokens.get(authTokenStringToUuids.get(authToken)).getToken();
        }
        return null;
    }

    public void invalidateAuthToken(String uuid) {
        // Remove any tokens linked with the uuid.
        if (uuidToAuthTokens.containsKey(uuid)) {
            authTokenStringToUuids.remove(uuidToAuthTokens.get(uuid).toString());
            uuidToAuthTokens.remove(uuid);
        }
        System.out.println("Table:");
        uuidToAuthTokens.forEach((key, value) -> System.out.println(key + " | " + value));
        System.out.println("Reversed Table:");
        authTokenStringToUuids.forEach((key, value) -> System.out.println(key + " | " + value));

    }
    public Token getToken(String uuid, String username) {
        if (uuidToTokens.containsKey(uuid)) {
            return uuidToTokens.get(uuid);
        }
        TokensConfig config = (TokensConfig) Resonance.getConfigManager().get(ConfigType.TOKENS);
        //if token is stored, return it, else return a new generated token.
        if (config.containsToken(uuid)) {
            return new Token(uuid, username, config.getCreationTime(uuid), config.getToken(uuid));
        }
        Token token = new Token(uuid, username, System.currentTimeMillis(), generateToken(TOKEN_LENGTH));
        config.saveToken(token);
        uuidToTokens.put(uuid, token);
        tokenStringToUuids.put(token.toString(), uuid);
        System.out.println("Table (Token):");
        uuidToTokens.forEach((key, value) -> System.out.println(key + " | " + value));
        System.out.println("Reversed Table (Token):");
        tokenStringToUuids.forEach((key, value) -> System.out.println(key + " | " + value));
        return token;
    }

    public static String generateToken(int length) {
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < length; i++) {
            token.append(TOKEN_CHARS.charAt(ThreadLocalRandom.current().nextInt(0, TOKEN_CHARS.length())));
        }
        return token.toString();
    }
    public boolean validateToken(String tokenString) {
        // Return if the provided auth token is valid. If the stored auth token has expired, remove it and return false.
        //if (uuidToAuthTokens.get(authTokenStringToUuids.get(authToken)).expired(60000L)) {
        if (tokenStringToUuids.containsKey(tokenString)) {
            return true;
        }
        TokensConfig config = (TokensConfig) Resonance.getConfigManager().get(ConfigType.TOKENS);
        if (config.containsUuid(tokenString)) {
            String uuid = config.getUuid(tokenString);
            Token token = new Token(uuid, config.getUsername(uuid), config.getCreationTime(uuid), tokenString);
            uuidToTokens.put(uuid, token);
            tokenStringToUuids.put(token.toString(), uuid);
            return true;
        }
        return false;

    }

    public Token getTokenFromTokenString(String token) {
        // Return the AuthToken currently stored, regardless of whether it is expired. if it does not exist, return null.
        if (validateToken(token)) {
            return uuidToTokens.get(tokenStringToUuids.get(token));
        }
        return null;
    }
    public Token updateToken(String tokenString) {
        if (validateToken(tokenString)) {
            TokensConfig config = (TokensConfig) Resonance.getConfigManager().get(ConfigType.TOKENS);
            Token oldToken = getTokenFromTokenString(tokenString);
            Token token = new Token(oldToken.getUuid(), oldToken.getUsername(), oldToken.getCreationTime(), generateToken(TOKEN_LENGTH));
            uuidToTokens.replace(token.getUuid(), token);
            tokenStringToUuids.remove(oldToken.toString());
            tokenStringToUuids.replace(token.toString(), token.getUuid());
            System.out.println(
                    "REPLACE:" +
                    token.getUuid() + "," +
                    token.toString() + "/" +
                    oldToken.getUuid() + "," +
                    oldToken.toString()

            );
            config.updateToken(oldToken, token);
            return token;

        }
        return null;
    }
}
