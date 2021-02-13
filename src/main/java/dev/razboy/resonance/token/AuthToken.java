package dev.razboy.resonance.token;

public class AuthToken {
    private String tokenString = "DYlbyU_vmYU";
    private final Token token;
    private final long creationTime;
    private final String uuid;
    private final String username;
    public AuthToken(String uuid, String username){
        this.uuid = uuid;
        this.username = username;
        token = new Token();
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
