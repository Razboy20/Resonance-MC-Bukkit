package dev.razboy.resonance.token;

public class AuthToken {
    private String tokenString = "DYlbyU_vmYU";
    private final Token token;
    private final long creationTime;
    public AuthToken(){
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
}
