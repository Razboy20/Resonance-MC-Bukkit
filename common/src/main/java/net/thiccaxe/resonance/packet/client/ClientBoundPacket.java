package net.thiccaxe.resonance.packet.client;

import net.thiccaxe.resonance.packet.Packet;

public abstract class ClientBoundPacket extends Packet {
    public boolean isServerBound() {
        return false;
    }
    public boolean isClientBound() {
        return true;
    }

    public final void readPacket(Packet packet) {
        setMessageId(packet.getMessageId());
        readPacketContent(packet);
    }
    public void readPacketContent(Packet packet) {}
}
