package dev.razboy.resonance.server.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class WebSocketHandler extends ChannelInboundHandlerAdapter {
    public static List<ChannelHandlerContext> ctxs = Collections
            .synchronizedList(new ArrayList<ChannelHandlerContext>());
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (msg instanceof WebSocketFrame) {
            System.out.println("This is a WebSocket frame");
            System.out.println("Client Channel : " + ctx.channel());
            if (msg instanceof BinaryWebSocketFrame) {
                System.out.println("BinaryWebSocketFrame Received : ");
                System.out.println(((BinaryWebSocketFrame) msg).content());
            } else if (msg instanceof TextWebSocketFrame) {
                System.out.println("TextWebSocketFrame Received : ");
                System.out.println(((TextWebSocketFrame) msg).text());
            } else if (msg instanceof PingWebSocketFrame) {
                System.out.println("PingWebSocketFrame Received : ");
                System.out.println(((PingWebSocketFrame) msg).content());
                ctx.writeAndFlush(new PongWebSocketFrame());
            } else if (msg instanceof PongWebSocketFrame) {
                System.out.println("PongWebSocketFrame Received : ");
                System.out.println(((PongWebSocketFrame) msg).content());
            } else if (msg instanceof CloseWebSocketFrame) {
                System.out.println("CloseWebSocketFrame Received : ");
                System.out.println("ReasonText :" + ((CloseWebSocketFrame) msg).reasonText());
                System.out.println("StatusCode : " + ((CloseWebSocketFrame) msg).statusCode());
                ctx.writeAndFlush(new CloseWebSocketFrame());
            } else {
                System.out.println("Unsupported WebSocketFrame");
            }
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ctxs.add(ctx);
    }
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        ctxs.remove(ctx);
    }

}
