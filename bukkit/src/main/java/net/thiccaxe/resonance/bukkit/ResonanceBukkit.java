package net.thiccaxe.resonance.bukkit;

import net.thiccaxe.resonance.Resonance;
import net.thiccaxe.resonance.ServerType;
import net.thiccaxe.resonance.server.ResonanceServer;
import net.thiccaxe.resonance.server.request.SyncNetworkHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;

public class ResonanceBukkit extends JavaPlugin implements Resonance {

    private BukkitTask syncNetworkTask;
    private File dataFolder;
    private File websiteFolder;
    @Override
    public void onEnable() {
        init(ServerType.BUKKIT, 25560, this);
        dataFolder = getDataFolder();
        dataFolder.mkdirs();
        websiteFolder = new File(dataFolder, "public");
        websiteFolder.mkdirs();
    }
    @Override
    public void onDisable() {
        shutdown();
    }


    @Override
    public void startSyncNetworkHandler(SyncNetworkHandler syncNetworkHandler) {
        syncNetworkTask = Bukkit.getScheduler().runTaskTimer(this, syncNetworkHandler, 0, 1);
    }

    @Override
    public void stopSyncNetworkHandler() {
        syncNetworkHandler.stop();
        syncNetworkTask.cancel();
    }

    @Override
    public File getFolder() {
        return dataFolder;
    }

    @Override
    public File getWebsiteFolder() {
        return websiteFolder;
    }
}
