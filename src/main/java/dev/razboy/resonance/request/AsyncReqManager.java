package dev.razboy.resonance.request;

import dev.razboy.resonance.Resonance;
import dev.razboy.resonance.network.Request;
import dev.razboy.resonance.packets.clientbound.ClientBoundPacket;
import dev.razboy.resonance.packets.clientbound.auth.AuthenticatedPacket;
import dev.razboy.resonance.util.HttpTools;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class AsyncReqManager extends IRequestManager {

    public AsyncReqManager(Resonance instance) {
        plugin = instance;
    }


    @Override
    protected void additional() {}

    @Override
    public void handleIncoming(Request request) {
        if (request.fullHttpRequest == null) {
            return;
        }
        FullHttpRequest req = request.fullHttpRequest;
        HttpTools.writeTemplateResponse(request.ctx, request.connection.getRemote(), req);
    }

    @Override
    protected void handleOutgoing(Request request) {
        System.out.println("Packet Post: " + request.packet.repr());
        if (request.packet != null) {
            //System.out.println("Ows: " + request.webSocketFrame.retain().text());
            request.ctx.writeAndFlush(new TextWebSocketFrame(request.packet.read()));
            return;
        } else if (request.fullHttpRequest != null) {
            //System.out.println("Ohttp: " + request.fullHttpRequest.retain().uri());
            request.ctx.writeAndFlush(request.fullHttpRequest);
            return;
        }
        System.out.println("Invalid R");
    }


}
