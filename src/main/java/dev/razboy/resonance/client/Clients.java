package dev.razboy.resonance.client;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.razboy.resonance.network.Connection;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.kyori.adventure.text.Component;
import org.json.JSONObject;

public class Clients {
    private final HashBiMap<Connection, String> clients = HashBiMap.create();


    public Clients(){}

    public void addClient(String token, Connection connection) {
        clients.forcePut(connection, token);
    }
    public boolean hasClient(String token) {
        return clients.containsValue(token);
    }
    public boolean hasClient(Connection connection) {
        return clients.containsKey(connection);
    }

    public Connection getClient(String token) {
        if (clients.containsValue(token)) {
            return clients.inverse().get(token);
        }
        return null;
    }


    public void removeClient(Connection connection) {
        clients.remove(connection);
    }

    public void sendAll(Component message) {
        String json = new JSONObject().put("action", "message").put("message", message).toString();
        clients.forEach(
                (connection, token) -> {
                    connection.getCtx().writeAndFlush(new TextWebSocketFrame(json));
                });
    }
}
