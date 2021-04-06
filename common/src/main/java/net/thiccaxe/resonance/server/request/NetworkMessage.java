package net.thiccaxe.resonance.server.request;

import io.netty.channel.ChannelHandlerContext;
import net.thiccaxe.resonance.packet.MalformedPacketException;
import net.thiccaxe.resonance.packet.Packet;
import net.thiccaxe.resonance.server.Connection;

public class NetworkMessage {

    private Packet packet;
    private final Connection connection;



    public NetworkMessage(Connection connection, Packet packet) {
        this.connection = connection;
        this.packet = packet;
    }
    public NetworkMessage(Connection connection, String message) {
        this.connection = connection;
        try {
            packet = Packet.readPacket(message);
        } catch (MalformedPacketException exception) {
            exception.printStackTrace();
        }
    }
    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }
    public Connection getConnection() {
        return connection;
    }
    public ChannelHandlerContext getCtx() {
        return connection.getCtx();
    }
}
