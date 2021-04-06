package net.thiccaxe.resonance.packet;

public class InvalidPacket extends Packet{
    @Override
    protected PacketType setPacketType() {
        return PacketType.INVALID;
    }

    @Override
    public String read() {
        return null;
    }
}
