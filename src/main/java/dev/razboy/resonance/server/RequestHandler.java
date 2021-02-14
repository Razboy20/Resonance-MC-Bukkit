package dev.razboy.resonance.server;

import dev.razboy.resonance.Resonance;
import dev.razboy.resonance.manager.RequestManager;
import dev.razboy.resonance.token.Token;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class RequestHandler extends SimpleChannelInboundHandler<Object> {
    private boolean authenticated = false;
    public String state = "http_only";
    private String remote = "";
    private int requests = 0;
    public Token token;
    private ChannelHandlerContext context;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object request) {
        context = ctx;
        requests++;


        if (remote.isEmpty() && request instanceof FullHttpRequest) {
            remote = RequestManager.getRemoteAddress(ctx, (FullHttpRequest) request);
        }
        if (request instanceof TextWebSocketFrame) {
            Resonance.getRequestManager().handleRequest(this, ctx, (TextWebSocketFrame) request);
        } else if (request instanceof FullHttpRequest) {
            Resonance.getRequestManager().handleRequest(this, ctx, (FullHttpRequest) request);
        }
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    public String getRemote() {return remote;}
    public int getRequests() {return requests;}
    public ChannelHandlerContext getCtx() {return context;}
}
