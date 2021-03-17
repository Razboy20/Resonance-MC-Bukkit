package dev.razboy.resonance.packets.clientbound.play;

import dev.razboy.resonance.packets.PacketType;
import dev.razboy.resonance.packets.clientbound.ClientBoundPacket;

public class PeerConnectPacket extends ClientBoundPacket {
    @Override
    protected PacketType setPacketType() {
        return PacketType.PEER_CONNECT;
    }
    @Override
    public String repr() {
        return getClass().getSimpleName() + "()";
    }

    @Override
    public String read() {
        return null;
    }
}
