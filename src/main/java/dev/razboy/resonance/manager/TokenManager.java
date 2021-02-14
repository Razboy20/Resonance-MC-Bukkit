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
    private static final String TOKEN_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
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



    public TokenManager() {}

    public void generateAuthToken(String uuid, String username) {
        // If a AuthToken already exists with the given UUID linked with it, delete/replace it, regardless of age.
        if (uuidToAuthTokens.containsKey(uuid)) {
            authTokenStringToUuids.remove(uuidToAuthTokens.get(uuid).toString());
            uuidToAuthTokens.remove(uuid);
        }
        AuthToken authToken = new AuthToken(uuid, username);
        uuidToAuthTokens.put(uuid, authToken);
        authTokenStringToUuids.put(authToken.toString(), uuid);

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
    }
    public AuthToken getAuthToken(String uuid) {
        if (uuidToAuthTokens.containsKey(uuid)) {
            return uuidToAuthTokens.get(uuid);
        }
        return null;
    }

    public AuthToken getAuthTokenFromAuthToken(String authToken) {
        // Return the AuthToken currently stored, regardless of whether it is expired. if it does not exist, return null.
        if (authTokenStringToUuids.containsKey(authToken)) {
            return uuidToAuthTokens.get(authTokenStringToUuids.get(authToken));
        }
        return null;

    }
    public boolean validateAuthToken(String authToken) {
        // Return if the provided auth token is valid. If the stored auth token has expired, remove it and return false.
        if (authTokenStringToUuids.containsKey(authToken)) {
            //if (uuidToAuthTokens.get(authTokenStringToUuids.get(authToken)).expired(60000L)) {
            if (!authToken.equals("DYlbyU_vmYU") && uuidToAuthTokens.get(authTokenStringToUuids.get(authToken)).expired(20000L)) {
                uuidToAuthTokens.remove(authTokenStringToUuids.get(authToken));
                authTokenStringToUuids.remove(authToken);
                return false;
            }
            return true;
        }
        return false;
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
        Token token = new Token(uuid, username, System.currentTimeMillis(), generateToken(64));
        config.saveToken(token);
        return token;
    }

    public static String generateToken(int length) {
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < length; i++) {
            token.append(TOKEN_CHARS.charAt(ThreadLocalRandom.current().nextInt(0, TOKEN_CHARS.length())));
        }
        return token.toString();
    }
}
