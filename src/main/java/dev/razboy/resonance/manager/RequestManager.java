package dev.razboy.resonance.manager;

import com.google.gson.JsonObject;
import dev.razboy.resonance.Resonance;
import dev.razboy.resonance.config.ConfigType;
import dev.razboy.resonance.files.FileUtils;
import dev.razboy.resonance.server.RequestHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class RequestManager {
    private final Resonance plugin;


    public RequestManager(Resonance plugin) {
        this.plugin = plugin;
    }

    public void handleRequest(RequestHandler handler, ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        System.out.println(frame.text());
        handleWebsocketFrame(handler, ctx, frame.text());

    }

    public void handleRequest(RequestHandler handler, ChannelHandlerContext ctx, FullHttpRequest request) {
        System.out.println(request.uri());
        if (handleUpgrade(handler, ctx, request)) {return;}
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
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void authenticateWebsocketMessage(RequestHandler handler, ChannelHandlerContext ctx, JSONObject json) {
        //Check if message is in valid format
        JSONObject body = json.getJSONObject("body");
        String response = "{";
        if (json.has("id")) {
            response = response + quote("id") + ":" + json.get("id").toString() + ",";
        }
        System.out.println("1: " + response);
        if (body.has("token")) {
            Resonance.log(body.get("token").toString());
            if(Resonance.getTokenManager().validateAuthToken(body.get("token").toString())){
                Resonance.log("authenticated");
                response = response +  quote("action") + ":" + quote("authenticated") + "," + quote("body") + ":{" + quote("token") + ":" + quote(Resonance.getTokenManager().getAuthTokenFromAuthToken(body.get("token").toString()).getToken().toString()) + "}";
            } else {
                Resonance.log("failed to auth");
                response = response +  quote("action") + ":" + quote("authentication_failed") + "," + quote("body") + ":{}";
            }
            System.out.println("2: " + response);
            ctx.write(new TextWebSocketFrame(response + "}"));
        }
    }


    private boolean handleUpgrade(RequestHandler handler, ChannelHandlerContext ctx, FullHttpRequest request) {
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
            return true;

        }
        return false;
    }
    public String quote(String content) {
        return "\"" + content + "\"";
    }
}
