package dev.razboy.resonance.client;

import dev.razboy.resonance.network.Connection;
import dev.razboy.resonance.token.Token;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.util.UUID;

public class Client {
    private final Connection connection;
    private final Token token;
    private final User user;


    public Client(Connection connection, Token token) {
        this.connection = connection;
        this.token = token;
        this.user = new User(token, Bukkit.getPlayer(UUID.fromString(token.uuid())));
    }

    public Token getToken() {
        return token;
    }
    public Connection getConnection() {
        return connection;
    }
    public User getUser() {
        return user;
    }
    public JSONObject getUserJson() {
        return user.getJson();
    }
}
