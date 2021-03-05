package dev.razboy.resonance.network;

import dev.razboy.resonance.Resonance;
import dev.razboy.resonance.request.HttpRequestManager;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class Connection extends SimpleChannelInboundHandler<Object> {
    private ChannelHandlerContext context;
    private int requests = 0;
    private String remote = "";
    private boolean websocketConnection = false;
    private static final byte[] CONTENT = { 'H', 'e', 'l', 'l', 'o', ' ', 'W', 'r', 'o', 'l', 'd' };
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object obj) {
        context = ctx;
        requests++;


        if (remote.isEmpty() && obj instanceof FullHttpRequest) {
            remote = HttpRequestManager.getRemoteAddress(ctx, (FullHttpRequest) obj);
        }
        if (obj instanceof TextWebSocketFrame) {
            TextWebSocketFrame frame = (TextWebSocketFrame) obj;
            String text = frame.retain().text();
            System.out.println("(" + requests + ")/" + remote + ": " + (text.length()>50?text.substring(0,49):text));
            Resonance.getWebSocketRequestManager().add(new Request(this, ctx, frame));
        } else if (obj instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) obj;
            String uri = request.uri();
            System.out.println("(" + requests + ")/" + remote + ": " + (uri.length()>50?uri.substring(0,49):uri));
            Resonance.getHttpRequestManager().add(new Request(this, ctx, request));
            if (!HttpRequestManager.upgrade(ctx, request)) {
                HttpRequestManager.writeTemplateResponse(ctx, remote, request);
            } else {
                websocketConnection = true;
            }
        }
        ctx.flush();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        if (websocketConnection) {
            Resonance.getWebSocketRequestManager().add(new Request(this, ctx, new TextWebSocketFrame("{\"action\":\"logout\"}")));
        }
    }

    public String getRemote() {return remote;}
    public int getRequests() {return requests;}
    public ChannelHandlerContext getCtx() {return context;}
}
