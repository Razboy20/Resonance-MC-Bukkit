package net.thiccaxe.resonance.bukkit;

import net.thiccaxe.resonance.Resonance;
import net.thiccaxe.resonance.ServerType;
import net.thiccaxe.resonance.server.ResonanceServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class ResonanceBukkit extends JavaPlugin implements Resonance {

    private BukkitTask serverTask;
    @Override
    public void onEnable() {
        init(ServerType.BUKKIT, 25560);
    }
    @Override
    public void onDisable() {
        shutdown();
        serverTask.cancel();
    }

    @Override
    public void startResonanceServer(ResonanceServer server) {
        serverTask = Bukkit.getScheduler().runTaskAsynchronously(this, server);
    }
}
