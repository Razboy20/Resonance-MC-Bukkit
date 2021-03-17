package dev.razboy.resonance.client;

import com.google.common.collect.HashBiMap;
import dev.razboy.resonance.Resonance;
import dev.razboy.resonance.network.Connection;
import dev.razboy.resonance.network.Request;
import dev.razboy.resonance.packets.clientbound.play.PeerUpdatePacket;
import dev.razboy.resonance.packets.clientbound.play.UserUpdatePacket;
import dev.razboy.resonance.token.Token;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.kyori.adventure.text.Component;
import org.json.JSONObject;

import java.util.HashMap;

public class Clients {
    private final HashBiMap<Connection, String> connections = HashBiMap.create();
    private final HashBiMap<String, Client> clients = HashBiMap.create();


    public Clients(){}

    public Client addClient(Token token, Connection connection) {
        connections.forcePut(connection, token.token());
        Client client = new Client(connection, token);
        clients.forcePut(token.token(), client);
        return client;
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
        /*
        String json = new JSONObject().put("action", "message").put("message", message).toString();
        connections.forEach(
                (connection, token) -> {
                    connection.getCtx().writeAndFlush(new TextWebSocketFrame(json));
                });
         **/
    }
    public void update() {
        HashMap<Client, JSONObject> clientInfo = new HashMap<>();
        clients.forEach((token, client) -> {
            UserUpdatePacket userUpdatePacket = new UserUpdatePacket();
            clientInfo.put(client, client.getUser().update());
            userUpdatePacket.setUser(clientInfo.get(client));
            Resonance.getHttpRequestManager().addOutgoing(new Request(client.getConnection(), userUpdatePacket));
        });
        /*
        clientInfo.keySet().forEach((client -> {
            clientInfo.keySet().forEach((peer -> {
                PeerUpdatePacket packet = new PeerUpdatePacket();
                if (peer != client) {
                    packet.addPeer(clientInfo.get(peer));
                }
                peer.getConnection().getCtx().writeAndFlush(new TextWebSocketFrame(packet.read()));
            }));
        }));
        */
    }
}
