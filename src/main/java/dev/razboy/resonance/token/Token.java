package dev.razboy.resonance.token;

import dev.razboy.resonance.Resonance;
import dev.razboy.resonance.config.ConfigType;
import dev.razboy.resonance.config.impl.TokensConfig;
import dev.razboy.resonance.manager.TokenManager;

public class Token {
    private String uuid;
    private String username;
    private String tokenString;
    private long creationTime;
    public Token(String uuid, String username, long creationTime, String token) {
        this.uuid = uuid;
        this.username = username;
        this.creationTime = creationTime;
        this.tokenString = token;
    }

    @Override
    public String toString() {
        return tokenString;
    }
    public String getUsername() {return username;}
    public String getUuid() {return uuid;}
    public long getCreationTime() {return creationTime;}

    public void setToken(String token) {
        tokenString = token;
    }
}
