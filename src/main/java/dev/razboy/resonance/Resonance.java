package dev.razboy.resonance;

import dev.razboy.resonance.commands.BroadcastCommand;
import dev.razboy.resonance.commands.VoiceConnectCommand;
import dev.razboy.resonance.manager.ConfigManager;
import dev.razboy.resonance.manager.RequestManager;
import dev.razboy.resonance.manager.TokenManager;
import dev.razboy.resonance.server.Server;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.json.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

public class Resonance extends JavaPlugin {
    private final Server httpServer = new Server();

    private static ConfigManager configManager;
    private static TokenManager tokenManager;

    public static Path websiteFolder;

    private static BukkitAudiences adventure;

    private static Resonance instance;

    private static RequestManager requestManager;

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

        configManager = new ConfigManager(getDataFolder());
        tokenManager = new TokenManager();
        tokenManager.generateAuthToken("b3af680f-f41b-41fb-9fcd-4dd2e13bf7fb", "razboy20", "DYlbyU_vmYU");


        websiteFolder = new File(getDataFolder(), "website").toPath();
        requestManager = new RequestManager(this);
        Objects.requireNonNull(getCommand("voiceconnect")).setExecutor(new VoiceConnectCommand());
        Objects.requireNonNull(getCommand("webcast")).setExecutor(new BroadcastCommand());
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
    public static RequestManager getRequestManager() {return requestManager;}
    public static ConfigManager getConfigManager() {return configManager;}
    public static TokenManager getTokenManager() {return tokenManager;}
    public static void log(String message) {
        getInstance().getLogger().info(message);
    }
}