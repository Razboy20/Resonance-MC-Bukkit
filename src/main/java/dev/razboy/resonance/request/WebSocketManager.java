package dev.razboy.resonance.request;

import com.google.common.collect.HashBiMap;
import dev.razboy.resonance.Resonance;
import dev.razboy.resonance.client.Client;
import dev.razboy.resonance.client.Clients;
import dev.razboy.resonance.network.Connection;
import dev.razboy.resonance.network.Request;
import dev.razboy.resonance.token.Token;
import dev.razboy.resonance.token.TokenManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WebSocketManager extends IRequestManager {
    private final Resonance plugin;
    private static final List<String> ACTIONS = Arrays.asList("authenticate", "logout", "user_info");
    private final Clients clients = new Clients();
    private final ConcurrentLinkedQueue<Component> sendQueue = new ConcurrentLinkedQueue<>();
    public WebSocketManager(Resonance instance) {
        plugin = instance;
    }

    private void close(Request request) {
        close(request.ctx);
    }
    private void close(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new CloseWebSocketFrame());
        ctx.close();
    }


    @Override
    protected void additional() {
        for (Component message : sendQueue) {
            if (message != null) {
                clients.sendAll(message);
            }
        }
        sendQueue.clear();
        clients.update();
    }

    @Override
    public void handle(Request request) {
        if (request.webSocketFrame == null) {
            return;
        }
        TextWebSocketFrame frame = request.webSocketFrame;
        String text = frame.text();
        try {
            JSONObject json = new JSONObject(text);
            if (!validateJson(json)) {
                close(request);
            }
            String action = json.get("action").toString();
            if (ACTIONS.contains(action)) {
                switch (action.toLowerCase()) {
                    case "authenticate":
                        authTokenAuthenticate(request, json);
                        break;
                    case "logout":
                        removeUser(request, json);
                        break;
                    case "user_info":
                        tokenAuthenticate(request, json);
                        break;
                    default:
                        break;


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeUser(Request request, JSONObject json) {
        if (clients.hasClient(request.connection)) {
            clients.removeClient(request.connection);
        }
    }

    private void tokenAuthenticate(Request request, JSONObject json) {
        if (json.has("bearer")) {
            String bearer = json.get("bearer").toString();
            TokenManager tokenManager = Resonance.getTokenManager();
            Token token = tokenManager.validateToken(bearer);
            if (token != null) {
                clients.addClient(token, request.connection);
                sendUserInfoMessage(request, clients.getClient(token.token()), json);
                return;
            }

        }
        sendAuthFailedMessage(request, json);
    }


    private void authTokenAuthenticate(Request request, JSONObject json) {
        if (json.get("body") != null && json.get("body") instanceof JSONObject) {
            JSONObject body = json.getJSONObject("body");
            if (body.has("token")) {
                Token token = Resonance.getTokenManager().validateAuthToken(body.get("token").toString());
                if (token != null) {
                    clients.addClient(token, request.connection);
                    sendAuthenticatedMessage(request, clients.getClient(token.token()), json);
                    clients.getClient(token.token()).sendLogInMessage(request.connection.getRemote(), body.get("token").toString());

                    return;
                }
            }
        }
        sendAuthFailedMessage(request, json);
    }

    private void sendUserInfoMessage(Request request, Client client, JSONObject json) {
        JSONObject body = new JSONObject().put("token", client.getToken().token()).put("user", client.getUserJson());
        JSONObject message = withIdActionBody(json, "user_info", body);
        request.ctx.writeAndFlush(new TextWebSocketFrame(message.toString()));
    }

    private void sendAuthFailedMessage(Request request, JSONObject json) {
        JSONObject message = withIdActionBody(json, "authentication_failed", new JSONObject());
        request.ctx.writeAndFlush(new TextWebSocketFrame(message.toString()));
    }

    private void sendAuthenticatedMessage(Request request, Client client, JSONObject json) {
        JSONObject body  = new JSONObject().put("token", client.getToken().token()).put("user", client.getUserJson());
        JSONObject message = withIdActionBody(json, "authenticated", body);
        request.ctx.writeAndFlush(new TextWebSocketFrame(message.toString()));
    }

    private JSONObject withIdActionBody(JSONObject json, String action, JSONObject body) {
        return addId(new JSONObject(), json).put("action", action).put("body", body);
    }

    private JSONObject addId(JSONObject message, JSONObject json) {
        if (json.has("id")) {
            try {
                message.put("id", json.getInt("id"));
            } catch (Exception ignored){}
        }
        return message;
    }

    private JSONObject getUser(Player player, Token token) {
        JSONObject user = new JSONObject();
        user.put("pos", new JSONObject()
                .put("x", 0)
                .put("y", 0)
                .put("z", 0)
                .put("rotation", new JSONArray(new float[]{90,0}))
        )
        .put("online", true)
        .put("data", new JSONObject()
                .put("uuid", token.uuid())
                .put("username", token.username())
        );
        return user;
    }

    private boolean validateJson(JSONObject json) {
        boolean body = true;
        if (json.has("body")) {
            body = json.get("body") instanceof JSONObject;
        }
        return json.has("action") && body;
    }
    public void send(Component message) {
        sendQueue.add(message);
    }

}
