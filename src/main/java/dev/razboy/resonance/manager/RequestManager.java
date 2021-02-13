package dev.razboy.resonance.manager;

import dev.razboy.resonance.Resonance;
import dev.razboy.resonance.files.FileUtils;
import dev.razboy.resonance.server.RequestHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class RequestManager {
    private final Resonance plugin;


    public RequestManager(Resonance plugin) {
        this.plugin = plugin;
    }

    public void handleRequest(RequestHandler handler, ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        System.out.println(frame.text());
        handleWebsocketFrame();

    }

    public void handleRequest(RequestHandler handler, ChannelHandlerContext ctx, FullHttpRequest request) {
        System.out.println(request.uri());
        if (handleUpgrade(handler, ctx, request)) {return;}
        FileUtils.sendTemplateHttpMessage(request, ctx);

    }


    private void handleWebsocketFrame() {
    }




    private boolean handleUpgrade(RequestHandler handler, ChannelHandlerContext ctx, FullHttpRequest request) {
        if (request.headers().get("Connection").equalsIgnoreCase("upgrade") && request.headers().get("Upgrade").equalsIgnoreCase("websocket")) {
            WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory("ws://localhost/", null, true);
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
}
