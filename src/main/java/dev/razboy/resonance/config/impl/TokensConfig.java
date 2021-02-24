package dev.razboy.resonance.config.impl;

import dev.razboy.resonance.config.Configuration;
import dev.razboy.resonance.token.Token;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.UUID;

public class TokensConfig extends Configuration {
    private static final String USERS = "Users.";
    private static final String TOKENS = "Tokens.";
    @Override
    protected String getFileName() {
        return "tokens.yml";
    }

    @Override
    protected void initializeConfig() {
        this.options().header("=== Tokens ~ DO NOT EDIT === #");
        this.options().copyDefaults(true);
    }
    public boolean containsToken(String uuid) {
        return !Objects.requireNonNull(this.getString(USERS + uuid + ".Token", "")).isEmpty();
    }
    public boolean containsUuid(String token) {
        return !Objects.requireNonNull(this.getString(TOKENS + token, "")).isEmpty();
    }
    public long getCreationTime(String uuid) {
        return this.getLong( USERS + uuid + ".Creation", 0L);
    }
    public String getToken(String uuid) {
        return this.getString(USERS + uuid + ".Token", "");
    }
    public String getUsername(String uuid) {
        return this.getString(USERS + uuid + ".Username", "");
    }
    public String getUuid(String token) {
        return this.getString(TOKENS + token, "");
    }
    public void saveToken(Token token) {
        String oldToken = getToken(token.getUuid());
        if (!oldToken.isEmpty()) {
            this.set(TOKENS + oldToken, null);
        }
        String uuid = token.getUuid();
        this.set(TOKENS + token.toString(), uuid);
        this.set(USERS + uuid + ".Token", token.toString());
        this.set(USERS + uuid + ".Creation", token.getCreationTime());
        this.set(USERS + uuid + ".Username", token.getUsername());
        this.save();
    }
    public void updateToken(Token oldToken, Token token) {
        if (containsUuid(oldToken.toString())) {
            this.set(TOKENS + oldToken.toString(), null);
            this.saveToken(token);

        }
    }
}
