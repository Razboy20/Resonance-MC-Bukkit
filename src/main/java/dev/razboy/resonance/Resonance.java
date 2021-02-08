package dev.razboy.resonance;

import dev.razboy.resonance.server.http.HttpServer;
import dev.razboy.resonance.server.websocket.WebSocketHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;

public class Resonance extends JavaPlugin {
    private final HttpServer httpServer = new HttpServer();
    public static Path websiteFolder;
    @Override
    public void onEnable() {
        websiteFolder = new File(getDataFolder(), "website").toPath();
        Bukkit.getScheduler().runTaskAsynchronously(this, httpServer);
//        getLogger().info("WebAudio enabled");
    }
    @Override
    public void onDisable() {
        httpServer.stop();
    }
}
