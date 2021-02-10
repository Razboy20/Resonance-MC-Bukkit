package dev.razboy.resonance.server.structs.client;

import dev.razboy.resonance.server.structs.tokens.Token;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Client {
    private final String uuid;
    private String username;
    private final OfflinePlayer player;
    private final Token token;

    public Client(String uuid, Token token) {
        this.token = token;
        this.uuid = uuid;
        this.player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
        this.username = player.getName();
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public Token getToken() {
        return token;
    }
}
