package dev.razboy.resonance.token;

import dev.razboy.resonance.manager.TokenManager;

public class Token {
    private String token;
    private final long creationTime;
    public Token() {
        token = TokenManager.generateToken(32);
        creationTime = System.currentTimeMillis();
    }
    @Override
    public String toString() {
        return token;
    }
}
