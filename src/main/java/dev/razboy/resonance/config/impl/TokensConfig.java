package dev.razboy.resonance.config.impl;

import dev.razboy.resonance.config.Configuration;

public class TokensConfig extends Configuration {
    @Override
    protected String getFileName() {
        return "tokens.yml";
    }

    @Override
    protected void initializeConfig() {
        this.options().header("=== Tokens ~ DO NOT EDIT === #");
        this.options().copyDefaults(true);
    }
}
