package dev.razboy.resonance.token;

import dev.razboy.resonance.Resonance;
import dev.razboy.resonance.manager.TokenManager;

public class AuthToken {
    private final String tokenString;
    private final Token token;
    private final long creationTime;
    private final String uuid;
    private final String username;
    public AuthToken(String uuid, String username, String tokenString){
        this.token = Resonance.getTokenManager().getToken(uuid, username);
        this.tokenString = tokenString;
        this.uuid = uuid;
        this.username = username;
        creationTime = System.currentTimeMillis();
    }
    public AuthToken(String uuid, String username) {
        this(uuid, username,  TokenManager.generateToken(11));
    }


    @Override
    public String toString() {
        return tokenString;
    }
    public boolean expired(long maxTime) {
        return System.currentTimeMillis()-creationTime > maxTime;
    }

    public Token getToken() {
        return token;
    }
    public String getUuid() {
        return uuid;
    }
    public String getUsername() {
        return username;
    }
}
