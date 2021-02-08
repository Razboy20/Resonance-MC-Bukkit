package dev.razboy.resonance.server.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WebSocketHandler extends ChannelInboundHandlerAdapter {
    public static List<ChannelHandlerContext> ctxs = Collections
            .synchronizedList(new ArrayList<ChannelHandlerContext>());
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {

        if (message instanceof WebSocketFrame) {
            WebSocketFrame msg = (WebSocketFrame) message;
            System.out.println("This is a WebSocket frame");
            System.out.println("Client Channel : " + ctx.channel());
            if (msg instanceof BinaryWebSocketFrame) {
                System.out.println("BinaryWebSocketFrame Received : ");
                System.out.println(msg.content());
            } else if (msg instanceof TextWebSocketFrame) {
                System.out.println("TextWebSocketFrame Received : ");
                System.out.println(((TextWebSocketFrame) msg).text());
                forwardTextMessage(ctx, (TextWebSocketFrame) msg);
            } else if (msg instanceof PingWebSocketFrame) {
                System.out.print("PingWebSocketFrame Received : ");
                System.out.println(msg.content());
                ctx.writeAndFlush(new PongWebSocketFrame(msg.content().retain()));
            } else if (msg instanceof PongWebSocketFrame) {
                System.out.println("PongWebSocketFrame Received : ");
                System.out.println(msg.content());
            } else if (msg instanceof CloseWebSocketFrame) {
                System.out.println("CloseWebSocketFrame Received : ");
                System.out.println("ReasonText :" + ((CloseWebSocketFrame) msg).reasonText());
                System.out.println("StatusCode : " + ((CloseWebSocketFrame) msg).statusCode());
                ctx.writeAndFlush(new CloseWebSocketFrame());
                ctx.channel().close();
            } else {
                System.out.println("Unsupported WebSocketFrame");
            }
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        ctxs.add(ctx);
    }
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        ctxs.remove(ctx);
    }
    protected void forwardTextMessage(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        for (ChannelHandlerContext context : ctxs) {
            if (!context.equals(ctx) && context.channel().isOpen()) {
                context.writeAndFlush(new TextWebSocketFrame(msg.text()));
            }
        }
    }

}
