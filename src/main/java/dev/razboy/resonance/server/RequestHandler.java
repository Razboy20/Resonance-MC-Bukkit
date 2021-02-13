package dev.razboy.resonance.server;

import dev.razboy.resonance.Resonance;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class RequestHandler extends SimpleChannelInboundHandler<Object> {
    private boolean authenticated = false;
    public String state = "http_only";


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object request) {
        if (request instanceof TextWebSocketFrame) {
            Resonance.getRequestManager().handleRequest(this, ctx, (TextWebSocketFrame) request);
        } else if (request instanceof FullHttpRequest) {
            Resonance.getRequestManager().handleRequest(this, ctx, (FullHttpRequest) request);
        } else if (request instanceof HttpRequest) {
            Resonance.log(((HttpRequest) request).uri() + "< ONO");
        }
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
