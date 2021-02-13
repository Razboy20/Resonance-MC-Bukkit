package dev.razboy.resonance;

import dev.razboy.resonance.manager.RequestManager;
import dev.razboy.resonance.server.Server;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.nio.file.Path;

public class Resonance extends JavaPlugin {
    private final Server httpServer = new Server();
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
        websiteFolder = new File(getDataFolder(), "website").toPath();
        requestManager = new RequestManager(this);
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
}
