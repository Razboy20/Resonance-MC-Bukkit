package dev.razboy.resonance.server.websocket;

import com.google.gson.JsonObject;
import dev.razboy.resonance.Resonance;
import dev.razboy.resonance.server.structs.client.Client;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.*;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class WebSocketHandler extends ChannelInboundHandlerAdapter {
    public static List<ChannelHandlerContext> ctxs = Collections
            .synchronizedList(new ArrayList<ChannelHandlerContext>());
    private static final JSONParser jsonParser= new JSONParser();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {

        if (message instanceof WebSocketFrame) {
            WebSocketFrame msg = (WebSocketFrame) message;
            if (msg instanceof TextWebSocketFrame) {
                try {
                    JSONObject json = new JSONObject(((TextWebSocketFrame) msg).text());
                    System.out.println(json.toString(2));
                    if (json.has("action") && !json.getJSONObject("body").isEmpty()) {
                        switch (json.get("action").toString().toLowerCase()) {
                            case "authenticate":
                                System.out.println("/" + ctx.channel().remoteAddress().toString() + ": Authentication attempt, token: " + json.getJSONObject("body").get("token"));
                                Client client = Resonance.getInstance().getTokenManager().checkToken(json.getJSONObject("body").get("token").toString());
                                String res = "{\"action\":\"authentication_failed\",\"body\":{\"token\":\"" + json.getJSONObject("body").get("token").toString() + "\"}}";
                                if (client != null) {
                                    res = "{\"action\":\"authenticated\",\"body\":{\"token\":\"" + client.getToken().getToken() + "\",\"uuid\":\"" + client.getUuid() +"\",\"username\":\"" + client.getUsername() + "\"}}";
                                }
                                ctx.writeAndFlush(new TextWebSocketFrame(res));
                                return;
                            default:
                        }
                    } else {
                        throw new Exception("ParseException: Mangled WebSocket JSON message");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Mangled WebSocket JSON message.");
                }
                //forwardTextMessage(ctx, (TextWebSocketFrame) msg);
            }





            else if (msg instanceof PingWebSocketFrame) {
                System.out.print("PingWebSocketFrame Received : ");
                System.out.println(msg.content());
                ctx.writeAndFlush(new PongWebSocketFrame(msg.content().retain()));
            }  else if (msg instanceof CloseWebSocketFrame) {
                ctx.writeAndFlush(new CloseWebSocketFrame());
                ctx.channel().close();
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
