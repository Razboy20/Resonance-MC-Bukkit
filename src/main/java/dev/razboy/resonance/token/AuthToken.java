package dev.razboy.resonance.token;

import dev.razboy.resonance.Resonance;

public class AuthToken {
    private String tokenString = "DYlbyU_vmYU";
    private Token token;
    private final long creationTime;
    private final String uuid;
    private final String username;
    public AuthToken(String uuid, String username){
        System.out.println("TEST 2");
        this.token = Resonance.getTokenManager().getToken(uuid, username);
        this.uuid = uuid;
        this.username = username;
        creationTime = System.currentTimeMillis();
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
