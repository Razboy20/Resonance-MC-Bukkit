package dev.razboy.resonance.config.impl;

import dev.razboy.resonance.config.Configuration;
import dev.razboy.resonance.token.Token;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.UUID;

public class TokensConfig extends Configuration {
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
        return !Objects.requireNonNull(this.getString(uuid + ".Token", "")).isEmpty();
    }
    public long getCreationTime(String uuid) {
        return this.getLong(uuid + ".Creation", 0L);
    }
    public String getToken(String uuid) {
        return this.getString(uuid + ".Token", "");
    }
    public void saveToken(Token token) {
        String uuid = token.getUuid();
        this.set(uuid + ".Token", token.toString());
        this.set(uuid + ".Creation", token.getCreationTime());
        this.set(uuid + ".Username", token.getUsername());
        this.save();
    }
}
