package dev.razboy.resonance.client;

import com.google.common.collect.HashBiMap;
import dev.razboy.resonance.Resonance;
import dev.razboy.resonance.network.Connection;
import dev.razboy.resonance.network.Request;
import dev.razboy.resonance.packets.clientbound.play.PeerUpdatePacket;
import dev.razboy.resonance.packets.clientbound.play.UserUpdatePacket;
import dev.razboy.resonance.request.SyncReqManager;
import dev.razboy.resonance.token.Token;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.kyori.adventure.text.Component;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

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
    public Client getClient(Connection connection) {
        if (connections.containsKey(connection)) {
            if (clients.containsKey(connections.get(connection))) {
                return clients.get(connections.get(connection));
            }
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
        HashMap<Client, JSONObject> clientUpdates = new HashMap<>();
        clients.forEach((token, client) -> {
            if (client.getUser().onlineUpdate()) {
                UserUpdatePacket userUpdatePacket = new UserUpdatePacket();
                JSONObject updatedOnline = client.getUser().updateOnline();
                clientUpdates.put(client, updatedOnline.put("type", UpdateType.ONLINE));
                userUpdatePacket.setUser(updatedOnline);
                userUpdatePacket.setType(UserUpdatePacket.ONLINE);
                Object data = updatedOnline.remove("data");
                SyncReqManager.send(new Request(client.getConnection(), userUpdatePacket));
                updatedOnline.put("data", data);
            }
            if (client.getUser().isOnline()) {
                if (client.getUser().positionUpdate()) {
                    UserUpdatePacket userUpdatePacket = new UserUpdatePacket();
                    JSONObject updatedPosition = client.getUser().updatePosition();
                    clientUpdates.put(client, updatedPosition.put("type", UpdateType.POSITION));
                    userUpdatePacket.setUser(updatedPosition);
                    userUpdatePacket.setType(UserUpdatePacket.POSITION);
                    Object data = updatedPosition.remove("data");
                    SyncReqManager.send(new Request(client.getConnection(), userUpdatePacket));
                    updatedPosition.put("data", data);
                }
                if (client.getUser().worldUpdate()) {
                    UserUpdatePacket userUpdatePacket = new UserUpdatePacket();
                    JSONObject updatedWorld = client.getUser().updateWorld();
                    clientUpdates.put(client, updatedWorld.put("type", UpdateType.WORLD));
                    userUpdatePacket.setUser(updatedWorld);
                    userUpdatePacket.setType(UserUpdatePacket.WORLD);
                    Object data = updatedWorld.remove("data");
                    SyncReqManager.send(new Request(client.getConnection(), userUpdatePacket));
                    updatedWorld.put("data", data);
                }
            }
        });
        if (clients.size() > 1) {
            clients.forEach((token, client) -> {
                PeerUpdatePacket peerOnlinePacket = new PeerUpdatePacket();
                clientUpdates.forEach((peer, json) -> {
                    if (!peer.getToken().uuid().equals(client.getToken().uuid())) {
                        peerOnlinePacket.addPeer(json);
                    }
                });
                if (peerOnlinePacket.needsSending()) {
                    SyncReqManager.send(new Request(client.getConnection(), peerOnlinePacket));
                }
            });
        }
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

    public void logoutClient(Connection connection) {
        if (connections.containsKey(connection)) {
            if (clients.containsKey(connections.get(connection))) {
                Token token = Objects.requireNonNull(clients.get(connections.get(connection))).getToken();
                Resonance.getTokenManager().invalidateToken(token);
            }
        }
        removeClient(connection);
    }

    public void sendAllBut(Request request) {
        String message = request.packet.read();
        clients.forEach((token, client) -> {
            if (client.getConnection() != request.connection) {
                client.getConnection().getCtx().writeAndFlush(new TextWebSocketFrame(message));
            }
        });
    }
}
