package dev.razboy.resonance.client;

import com.google.common.collect.HashBiMap;
import dev.razboy.resonance.network.Connection;
import dev.razboy.resonance.token.Token;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.kyori.adventure.text.Component;
import org.json.JSONObject;

public class Clients {
    private final HashBiMap<Connection, String> connections = HashBiMap.create();
    private final HashBiMap<String, Client> clients = HashBiMap.create();


    public Clients(){}

    public void addClient(Token token, Connection connection) {
        connections.forcePut(connection, token.token());
        clients.forcePut(token.token(), new Client(connection, token));
    }
    public boolean hasClient(String token) {
        return connections.containsValue(token);
    }
    public boolean hasClient(Connection connection) {
        return connections.containsKey(connection);
    }

    public Client getClient(String token) {
        if (connections.containsValue(token)) {
            return clients.get(token);
        }
        return null;
    }


    public void removeClient(Connection connection) {
        clients.remove(connections.get(connection));
        connections.remove(connection);
    }

    public void sendAll(Component message) {
        String json = new JSONObject().put("action", "message").put("message", message).toString();
        connections.forEach(
                (connection, token) -> {
                    connection.getCtx().writeAndFlush(new TextWebSocketFrame(json));
                });
    }
    public void update() {
        clients.forEach((token, client) -> {
            if (client.getUser().update()){
                client.getConnection().getCtx().writeAndFlush(new TextWebSocketFrame(new JSONObject().put("action", "user_update").put("body", new JSONObject() .put("user", client.getUserJson())).toString()));
            }
        });
    }
}
