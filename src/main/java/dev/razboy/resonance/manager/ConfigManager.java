package dev.razboy.resonance.manager;

import dev.razboy.resonance.config.ConfigType;
import dev.razboy.resonance.config.Configuration;
import dev.razboy.resonance.config.impl.DefaultConfig;
import dev.razboy.resonance.config.impl.TokensConfig;

import java.io.File;
import java.util.EnumMap;

public class ConfigManager {
    private final File dataFolder;

    private final EnumMap<ConfigType, Configuration> configs = new EnumMap<>(ConfigType.class);

    public ConfigManager(File dataFolder) {
        this.dataFolder = dataFolder;
        createPluginFolder();

        configs.put(ConfigType.DEFAULT, new DefaultConfig());
        configs.put(ConfigType.TOKENS, new TokensConfig());

        for (Configuration config : configs.values()) {
            config.setupFile(this.dataFolder);
        }
    }
    public Configuration get(ConfigType type) {
        return configs.get(type);
    }
    public void reloadConfigs() {
        for (Configuration parkourConfig : configs.values()) {
            parkourConfig.reload();
        }
    }
    private void createPluginFolder() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }
}
