package dev.razboy.resonance.manager;

import dev.razboy.resonance.Resonance;
import dev.razboy.resonance.config.ConfigType;
import dev.razboy.resonance.config.impl.TokensConfig;
import dev.razboy.resonance.files.FileUtils;
import dev.razboy.resonance.server.RequestHandler;
import dev.razboy.resonance.token.AuthToken;
import dev.razboy.resonance.token.Token;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RequestManager {
    private final Resonance plugin;
    private final HashMap<RequestHandler, String> requestHandlerToUuids = new HashMap<>();
    private final HashMap<String, RequestHandler> uuidToRequestHandlers = new HashMap<>();

    public RequestManager(Resonance plugin) {
        this.plugin = plugin;
    }

    public void handleRequest(RequestHandler h, ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        System.out.println("(" + h.getRequests() + ")/" + h.getRemote() + ": " + frame.text());
        handleWebsocketFrame(h, ctx, frame.text());

    }
    public void removeHandler(RequestHandler h, ChannelHandlerContext ctx) {
        removeUserFromAuthList(h, ctx);
    }

    public void handleRequest(RequestHandler h, ChannelHandlerContext ctx, FullHttpRequest request) {
        System.out.println("(" + h.getRequests() + ")/" + h.getRemote() + ": " + request.uri());
        if (handleUpgrade(h, ctx, request)) {return;}
        FileUtils.sendTemplateHttpMessage(request, ctx);

    }


    private void handleWebsocketFrame(RequestHandler handler, ChannelHandlerContext ctx, String text) {
        //Parse JSON
        try {
            JSONObject json = new JSONObject(text);
            //Check if message is in valid format
            if (json.has("action")) {
                switch (json.get("action").toString()) {
                    case "authenticate":
                        authenticateWebsocketMessage(handler, ctx, json);
                        break;
                    case "user_info":
                        userInfoWebsocketMessage(handler, ctx, json);
                        break;
                    case "connected_users":
                        connectedUsersWebsocketMessage(handler,ctx, json);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void authenticateWebsocketMessage(RequestHandler handler, ChannelHandlerContext ctx, JSONObject json) {
        //Check if message is in valid format
        try {
            JSONObject body = json.getJSONObject("body");
            String response = addID("{", json);
            if (body.has("token")) {
                if (Resonance.getTokenManager().validateAuthToken(body.get("token").toString())) {
                    handler.state = "authenticated";
                    AuthToken authToken = Resonance.getTokenManager().getAuthTokenFromAuthTokenString(body.get("token").toString());
                    addUserToAuthList(handler, ctx, authToken.getToken());
                    if (!authToken.toString().equals(TokenManager.DEV_TOKEN)) {
                        Resonance.getTokenManager().invalidateAuthToken(authToken.getUuid());
                    }
                    response = response + quote("action") + ":" + quote("authenticated") + "," + quote("body") + ":{" + quote("token") + ":" + quote(authToken.getToken().toString()) + ",\"user\":{\"uuid\":" + quote(authToken.getUuid()) + ",\"username\":" + quote(authToken.getUsername()) + "}}";
                } else {
                    handler.state = "connected";
                    removeUserFromAuthList(handler, ctx);
                    response = response + quote("action") + ":" + quote("authentication_failed") + "," + quote("body") + ":{}";
                }
                ctx.write(new TextWebSocketFrame(response + "}"));
            }
        } catch (Exception ignored) {}
    }

    private void connectedUsersWebsocketMessage(RequestHandler handler, ChannelHandlerContext ctx, JSONObject json) {
        TokenManager tokenManager = Resonance.getTokenManager();
        String response = addID("{", json);
        if (json.has("bearer")) {
            String bearer = json.get("bearer").toString();
            if (tokenManager.validateToken(bearer)) {
                List<String> uuids = new ArrayList<>(uuidToRequestHandlers.keySet());
                response+=("\"action\":\"connected_users\",\"body\":{\"users\":[");
                response+=(String.join(",", uuids));
                response+=("]}}");
                System.out.println(response);
                ctx.write(new TextWebSocketFrame(response));
            } else {
                ctx.write(new TextWebSocketFrame(response + "\"action\":\"authentication_failed\"}"));
            }
        }
    }

    private void userInfoWebsocketMessage(RequestHandler handler, ChannelHandlerContext ctx, JSONObject json) {
        TokensConfig config = (TokensConfig) Resonance.getConfigManager().get(ConfigType.TOKENS);
        TokenManager tokenManager = Resonance.getTokenManager();
        String response = addID("{", json);
        String uuid;
        String username;
        if (json.has("bearer")) {
            String bearer = json.get("bearer").toString();
            if (tokenManager.validateToken(bearer)) {
                //Token token = tokenManager.updateToken(bearer);
                Token token = tokenManager.getTokenFromTokenString(bearer);
                uuid = token.getUuid();
                username = token.getUsername();
                handler.state = "authenticated";
                addUserToAuthList(handler, ctx, Resonance.getTokenManager().getToken(uuid, username));
                response += "\"action\":\"user_info\",\"body\":{\"uuid\":" + quote(uuid) + ",\"username\":" + quote(username) + ",\"token\":" + quote(token.toString()) + "}}";
                ctx.write(new TextWebSocketFrame(response));
                return;
            }
            ctx.write(new TextWebSocketFrame(response + "\"action\":\"authentication_failed\"}"));
        }
    }

    private String addID(String message, JSONObject json) {
        if (json.has("id")) {
            message = message + quote("id") + ":" + json.get("id").toString() + ",";
        }
        return message;
    }


    private boolean handleUpgrade(RequestHandler handler, ChannelHandlerContext ctx, FullHttpRequest request) {
        if (request.headers().get("Connection") == null || request.headers().get("Upgrade") == null) {
            return false;
        }
        if (request.headers().get("Connection").equalsIgnoreCase("upgrade") && request.headers().get("Upgrade").equalsIgnoreCase("websocket")) {
            boolean secure = Resonance.getConfigManager().get(ConfigType.DEFAULT).getBoolean("Secure");
            String domain = Resonance.getConfigManager().get(ConfigType.DEFAULT).getString("Domain");
            WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(secure ? "wss" : "ws" + "://" + domain + "/", null, true);
            WebSocketServerHandshaker handshaker = factory.newHandshaker(request);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
                return false;
            }
            handshaker.handshake(ctx.channel(), request);
            handler.state = "connected";
            return true;

        }
        return false;
    }
    public String quote(String content) {
        return "\"" + content + "\"";
    }
    public static String getRemoteAddress(ChannelHandlerContext ctx, FullHttpRequest request) {
        HttpHeaders headers = request.headers();
        String ip = headers.get("X-Forwarded-For");
        if (ip != null) {return ip;}
        return ctx.channel().remoteAddress().toString();
    }

    private void addUserToAuthList(RequestHandler h, ChannelHandlerContext ctx, Token token) {
        //System.out.println("Adding User to Authlist. Token (" + token.toString() + "/" + token.getUuid() + ") Will be linked with a Handler (/" + h.getRemote() + ").");
        removeOldUsersFromAuthList(h, ctx, token);
        uuidToRequestHandlers.put(token.getUuid(), h);
        requestHandlerToUuids.put(h, token.getUuid());
        /*
        System.out.println("\n\nCurrent State of table:");
        int i = 1;
        for (RequestHandler handler : uuidToRequestHandlers.values()) {
            System.out.println(i + ") " + handler.getRemote() + "/" + requestHandlerToUuids.get(handler));
            i++;
        }
        if (i == 1) {
            System.out.println("(Empty)");
        }
        System.out.println("\nReverse table: ");
        i = 1;
        for (String uuid : requestHandlerToUuids.values()) {
            System.out.println(i + ") " + uuid);
            i++;
        }
        if (i == 1) {
            System.out.println("(Empty)");
        }
        */
        h.token = token;
        System.out.println("Added User(" + h.getRemote() + "), token: " + token.toString() + ", UUID: " + token.getUuid() + ", to AUTH list");
        System.out.println("Tables (connected):");
        uuidToRequestHandlers.forEach((key, value) -> System.out.println(key + " | " + value.getRemote()+value.token));
        requestHandlerToUuids.forEach((key, value) -> System.out.println(key.getRemote()+key.token + " | " + value));
    }
    private void removeUserFromAuthList(RequestHandler h, ChannelHandlerContext ctx) {
        if (h.token != null) {
            System.out.println("Removing user from auth list. handler's token: " + h.token.toString());
            requestHandlerToUuids.remove(uuidToRequestHandlers.get(h.token.getUuid()));
            uuidToRequestHandlers.remove(h.token.getUuid());
        }
        if (requestHandlerToUuids.containsKey(h)) {
            String uuid = requestHandlerToUuids.get(h);
            System.out.println("Received handler is already stored, with UUID: " + uuid + ". Removing...");
            uuidToRequestHandlers.remove(uuid);
            requestHandlerToUuids.remove(h);
            System.out.println("Removed User(" + h.getRemote() + "), UUID: " + uuid + ", from AUTH list");
        }
        System.out.println("Tables (connected):");
        uuidToRequestHandlers.forEach((key, value) -> System.out.println(key + " | " + value.getRemote()+value.token));
        System.out.println("Reversed Table (connected):");
        requestHandlerToUuids.forEach((key, value) -> System.out.println(key.getRemote()+key.token + " | " + value));
    }
    private void removeOldUsersFromAuthList(RequestHandler h, ChannelHandlerContext ctx, Token token) {
        if (uuidToRequestHandlers.containsKey(token.getUuid())) {
            removeUserFromAuthList(uuidToRequestHandlers.get(token.getUuid()), ctx);
        }
        System.out.println("Table (connected):");
        uuidToRequestHandlers.forEach((key, value) -> System.out.println(key + " | " + value.getRemote()+value.token));
        System.out.println("Reversed Table (connected):");
        requestHandlerToUuids.forEach((key, value) -> System.out.println(key.getRemote()+key.token + " | " + value));
    }

    public void broadcast(String message) {
        TextWebSocketFrame frame = new TextWebSocketFrame("{\"action\":\"plugin_message\",\"body\":{\"message\":" + quote(message) + "}}");
        for (RequestHandler handler : uuidToRequestHandlers.values()) {
            handler.getCtx().writeAndFlush(frame);
        }
    }
}
