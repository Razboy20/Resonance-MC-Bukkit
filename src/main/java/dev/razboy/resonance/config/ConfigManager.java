package dev.razboy.resonance.config;

import dev.razboy.resonance.config.impl.DefaultConfig;
import dev.razboy.resonance.config.impl.TokenConfig;

import java.io.File;
import java.util.EnumMap;

public class ConfigManager {
    private final File dataFolder;

    private final EnumMap<ConfigType, Config> configs = new EnumMap<>(ConfigType.class);

    public ConfigManager(File dataFolder) {
        this.dataFolder = dataFolder;
        createPluginFolder();

        configs.put(ConfigType.DEFAULT, new DefaultConfig());
        configs.put(ConfigType.TOKENS, new TokenConfig());

        for (Config config : configs.values()) {
            config.setupFile(this.dataFolder);
        }
    }
    public Config get(ConfigType type) {
        return configs.get(type);
    }
    public void reloadConfigs() {
        for (Config parkourConfig : configs.values()) {
            parkourConfig.reload();
        }
    }
    private void createPluginFolder() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }
}