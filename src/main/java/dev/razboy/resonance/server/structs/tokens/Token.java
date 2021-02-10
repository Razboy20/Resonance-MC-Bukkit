package dev.razboy.resonance.server.structs.tokens;

public class Token {
    private String token;
    private String uuid;
    private long creationTime;
    public Token(String token, String uuid) {
        this.token = token;
        this.uuid = uuid;
        this.creationTime = System.currentTimeMillis();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public String getToken() {
        return token;
    }
    @Override
    public String toString() {
        return token;
    }
}
