package dev.razboy.resonance.server.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import dev.razboy.resonance.server.websocket.WebSocketHandler;

import java.nio.charset.StandardCharsets;

import static io.netty.buffer.Unpooled.copiedBuffer;

public class HttpServerHandler extends ChannelInboundHandlerAdapter {
    WebSocketServerHandshaker handshake;

    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception{
        System.out.println("test");
        if (message instanceof FullHttpRequest) {
            FullHttpRequest msg = (FullHttpRequest) message;
            System.out.println("Http Request Received");

            HttpHeaders headers = msg.headers();
            if (
                headers.get(HttpHeaderNames.CONNECTION).equalsIgnoreCase(String.valueOf(HttpHeaderValues.UPGRADE)) &&
                headers.get(HttpHeaderNames.UPGRADE).equalsIgnoreCase("websocket")
            ) {
                System.out.println("WS request");
                ctx.pipeline().replace(this, "websocketHandler", new WebSocketHandler());
                handleHandshake(ctx, msg);
            }
            String responseMessage = "<bold>BOLD<bold> test";
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, copiedBuffer(responseMessage.getBytes(StandardCharsets.UTF_8)));
            if (HttpUtil.isKeepAlive(msg)) {
                response.headers().set(
                        HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE
                );
            }
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, responseMessage.length());
            ctx.writeAndFlush(response);

        } else {
            System.out.println("Incoming request is unknown");
            super.channelRead(ctx, message);
        }
    }

    /* Do the handshaking for WebSocket request */
    protected void handleHandshake(ChannelHandlerContext ctx, HttpRequest req) {
        WebSocketServerHandshakerFactory wsFactory =
                new WebSocketServerHandshakerFactory(getWebSocketURL(req), null, true);
        handshake = wsFactory.newHandshaker(req);
        if (handshake == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshake.handshake(ctx.channel(), req);
        }
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.writeAndFlush(new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR,
                copiedBuffer(cause.getMessage().getBytes())
        ));
    }

    protected String getWebSocketURL(HttpRequest req) {
        //System.out.println("Req URI : " + req.uri());
        String url =  "ws://" + req.headers().get("Host") + req.uri() ;
        System.out.println("URL : " + url);
        return url;
    }
}
