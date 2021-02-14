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

import java.util.HashMap;

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
                    case "user_info":
                        userInfoWebsocketMessage(handler, ctx, json);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void authenticateWebsocketMessage(RequestHandler handler, ChannelHandlerContext ctx, JSONObject json) {
        //Check if message is in valid format
        JSONObject body = json.getJSONObject("body");
        String response = addID("{", json);
        if (body.has("token")) {
            if (Resonance.getTokenManager().validateAuthToken(body.get("token").toString())){
                handler.state = "authenticated";
                AuthToken authToken = Resonance.getTokenManager().getAuthTokenFromAuthToken(body.get("token").toString());
                authenticateUser(handler, ctx, authToken.getToken());
                if (!authToken.toString().equals("DYlbyU_vmYU")) {
                    Resonance.getTokenManager().invalidateAuthToken(authToken.getUuid());
                }
                response = response +  quote("action") + ":" + quote("authenticated") + "," + quote("body") + ":{" + quote("token") + ":" + quote(authToken.getToken().toString()) + ",\"user\":{\"uuid\":" + quote(authToken.getUuid()) + ",\"username\":" + quote(authToken.getUsername()) + "}}";
            } else {
                handler.state = "connected";
                response = response +  quote("action") + ":" + quote("authentication_failed") + "," + quote("body") + ":{}";
            }
            ctx.write(new TextWebSocketFrame(response + "}"));
        }
    }

    private void userInfoWebsocketMessage(RequestHandler handler, ChannelHandlerContext ctx, JSONObject json) {
        TokensConfig config = (TokensConfig) Resonance.getConfigManager().get(ConfigType.TOKENS);
        String uuid = "null";
        String username = "";
        if (json.has("bearer")) {
            String bearer = json.get("bearer").toString();
            if (config.containsUuid(bearer)) {
                uuid = config.getUuid(bearer);
                username = config.getUsername(uuid);
            }
            String response = addID("{", json);
            handler.state = "authenticated";
            authenticateUser(handler, ctx, Resonance.getTokenManager().getToken(uuid, username));
            response =  response + "\"action\":\"user_info\",\"body\":{\"uuid\":" + quote(uuid) + ",\"username\":" + quote(username) + "}}";
            ctx.write(new TextWebSocketFrame(response));
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

    private void authenticateUser(RequestHandler h, ChannelHandlerContext ctx, Token token) {
        uuidToRequestHandlers.put(token.getUuid(), h);
        requestHandlerToUuids.put(h, token.getUuid());
        h.token = token;
    }
    public void broadcast(String message) {
        TextWebSocketFrame frame = new TextWebSocketFrame("{\"action\":\"plugin_message\",\"body\":{\"message\":" + quote(message) + "}}");
        for (RequestHandler handler : uuidToRequestHandlers.values()) {
            handler.getCtx().writeAndFlush(frame);
        }
    }
}
