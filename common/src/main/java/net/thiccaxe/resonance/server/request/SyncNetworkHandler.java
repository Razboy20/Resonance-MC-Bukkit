package net.thiccaxe.resonance.server.request;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.thiccaxe.resonance.auth.TokenManager;
import net.thiccaxe.resonance.packet.Packet;
import net.thiccaxe.resonance.packet.client.ClientBoundPacket;
import net.thiccaxe.resonance.packet.client.auth.AuthenticatedPacket;
import net.thiccaxe.resonance.packet.client.play.ClientUserInfoPacket;

public class SyncNetworkHandler extends NetworkHandler{
    @Override
    protected void handleIncoming(NetworkMessage request) {
        try {
            Packet packet = request.getPacket();
            if (packet != null) {
                switch (packet.getType()) {
                    case AUTHENTICATE: {
                        AuthenticatedPacket authenticatedPacket = new AuthenticatedPacket();
                        authenticatedPacket.readPacket(packet);
                        send(request.getCtx(), authenticatedPacket);
                    }
                    break;
                    case SERVER_USERINFO: {
                        ClientUserInfoPacket userInfoPacket = new ClientUserInfoPacket();
                        userInfoPacket.readPacket(packet);
                        send(request.getCtx(), userInfoPacket);
                    }

                    case INVALID:
                    default:
                        break;
                }
            }
        } catch (Exception ignored) {}
    }

    private void send(ChannelHandlerContext ctx, ClientBoundPacket packet) {
        ctx.writeAndFlush(new TextWebSocketFrame(packet.read()));
    }

    @Override
    protected void handleOutgoing(NetworkMessage response) {

    }
}
