package dev.razboy.resonance;

import dev.razboy.resonance.server.http.HttpServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private final HttpServer httpServer = new HttpServer();
    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTaskAsynchronously(this, httpServer);
        getLogger().info("WebAudio enabled");
    }
    @Override
    public void onDisable() {
        httpServer.stop();
    }
}
