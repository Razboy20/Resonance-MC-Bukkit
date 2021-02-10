package dev.razboy.resonance;

import dev.razboy.resonance.commands.VoiceConnectCommand;
import dev.razboy.resonance.server.http.HttpServer;
import dev.razboy.resonance.server.structs.tokens.TokenManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.nio.file.Path;

public class Resonance extends JavaPlugin {
    private final HttpServer httpServer = new HttpServer();
    public static Path websiteFolder;
    private static BukkitAudiences adventure;
    private static Resonance instance;
    private final TokenManager tokenManager = new TokenManager(this);
    public @NonNull BukkitAudiences adventure() {
        if(adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }

    @Override
    public void onEnable() {
        instance = this;
        adventure = BukkitAudiences.create(this);
        websiteFolder = new File(getDataFolder(), "website").toPath();
        getCommand("voiceconnect").setExecutor(new VoiceConnectCommand());
        Bukkit.getScheduler().runTaskAsynchronously(this, httpServer);
    }
    @Override
    public void onDisable() {
        if(adventure != null) {
            adventure.close();
            adventure = null;
        }
        httpServer.stop();
    }
    public static BukkitAudiences getAdventure() {
        return adventure;
    }
    public static Resonance getInstance() {
        return instance;
    }
    public TokenManager getTokenManager() {
        return tokenManager;
    }
}
