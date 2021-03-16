package dev.razboy.resonance.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class Request {
    public final Connection connection;
    public FullHttpRequest fullHttpRequest;
    public TextWebSocketFrame webSocketFrame;
    public final ChannelHandlerContext ctx;

    public Request(Connection connection, ChannelHandlerContext ctx, TextWebSocketFrame webSocketFrame) {
        this.connection = connection;
        this.ctx = ctx;
        this.webSocketFrame = webSocketFrame;
        this.fullHttpRequest = null;
    }
    public Request(Connection connection, ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        this.connection = connection;
        this.ctx = ctx;
        this.webSocketFrame = null;
        this.fullHttpRequest = fullHttpRequest;
    }
    public Request setFullHttpRequest(FullHttpRequest r) {
        fullHttpRequest = r;
        return this;
    }
    public Request setWebSocketFrame(TextWebSocketFrame f) {
        webSocketFrame = f;
        return this;
    }

}
