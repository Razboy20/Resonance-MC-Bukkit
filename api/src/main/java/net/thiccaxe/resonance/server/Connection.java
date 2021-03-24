package net.thiccaxe.resonance.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import net.thiccaxe.resonance.util.HttpUtility;

public class Connection extends SimpleChannelInboundHandler<Object> {
    private String remote = "";
    private ChannelHandlerContext context = null;
    private boolean isWebsocketConnection = false;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (remote.isEmpty() && msg instanceof FullHttpRequest) {
            remote = HttpUtility.getRemoteAddress(ctx, (FullHttpRequest) msg);
        }

        if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame frame = (TextWebSocketFrame) msg;
            String message = frame.retain().text();
            System.out.println("/" + remote + ": " + message);
            ChannelFuture c = ctx.writeAndFlush(new TextWebSocketFrame(message));
        } else {
            if (msg instanceof FullHttpRequest) {
                FullHttpRequest request = (FullHttpRequest) msg;
                System.out.println("/" + remote + ": " + request.uri());
                if (!HttpUtility.upgrade(ctx, request)) {
                    HttpUtility.writeTemplateResponse(ctx, remote, request);
                } else {
                    System.out.println(remote + ": Upgraded to websocket");
                    isWebsocketConnection = true;
                }
            }
        }
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        context = ctx;
    }
}
