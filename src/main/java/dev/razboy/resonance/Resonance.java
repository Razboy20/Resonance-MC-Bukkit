package dev.razboy.resonance;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.razboy.resonance.command.TokenCommand;
import dev.razboy.resonance.config.ConfigManager;
import dev.razboy.resonance.config.ConfigType;
import dev.razboy.resonance.config.impl.TokenConfig;
import dev.razboy.resonance.listener.MessageListener;
import dev.razboy.resonance.network.Server;
import dev.razboy.resonance.request.*;
import dev.razboy.resonance.token.TokenManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;
import java.util.Properties;

public class Resonance extends JavaPlugin implements CommandExecutor {
    private static Resonance instance;
    private static File dataFolder;
    private static File tokenFile;

    private static final Properties properties = new Properties();
    private static final BiMap<String, String> something = HashBiMap.create();

    private static final Server server = new Server();
    private static HttpRequestManager httpRequestManager;
    private static WebSocketManager webSocketRequestManager;


    private static ConfigManager configManager;
    private static TokenManager tokenManager;

    private BukkitTask serverTask;
    private BukkitTask httpTask;
    private BukkitTask wsTask;

    public static TokenManager getTokenManager() {return tokenManager;}

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 2) {
            sender.sendMessage(args[0]);
            sender.sendMessage(args[1]);
            something.forcePut(args[0], args[1]);
            return true;
        }
        else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("save")) {
                saveTokens();
            }
        }
        return false;
    }

    @Override public void onEnable() {
        System.out.println("Starting Server...");
        instance = this;
        dataFolder = getDataFolder();
        configManager = new ConfigManager(dataFolder);
        tokenManager = new TokenManager(this, (TokenConfig)configManager.get(ConfigType.TOKENS));
        tokenFile = new File(dataFolder, "tokens");
        Objects.requireNonNull(this.getCommand("voiceconnect")).setExecutor(this);
        saveTokens();
        loadTokens();



        webSocketRequestManager = new WebSocketManager(this);
        httpRequestManager = new HttpRequestManager(this);
        wsTask = Bukkit.getScheduler().runTaskTimer (this, webSocketRequestManager, 10, 1);
        httpTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, httpRequestManager, 10, 1);
        serverTask = Bukkit.getScheduler().runTaskAsynchronously(this, server);
        getServer().getPluginManager().registerEvents(new MessageListener(webSocketRequestManager), this);
        Objects.requireNonNull(this.getCommand("token")).setExecutor(new TokenCommand());
    }
    @Override public void onDisable() {
        server.stop();
        serverTask.cancel();
        webSocketRequestManager.stop();
        wsTask.cancel();
        httpRequestManager.stop();
        httpTask.cancel();
    }




    private void loadTokens(){
        try {
            properties.load(new FileInputStream(tokenFile));
            properties.forEach((Object key, Object val) -> {
                something.forcePut(key.toString(), val.toString());
            });
        } catch (FileNotFoundException e) {
            System.out.println("Failed to load tokens file!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveTokens() {
        properties.clear();
        properties.putAll(something);
        try {
            properties.store(new FileOutputStream(tokenFile), null);
        } catch (IOException e) {
            System.out.println("Failed to save tokens file!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HttpRequestManager getHttpRequestManager() {
        return httpRequestManager;
    }
    public static WebSocketManager getWebSocketRequestManager() {return webSocketRequestManager;}
    public static void log(String message) {
        instance.getLogger().info(message);
    }

}
