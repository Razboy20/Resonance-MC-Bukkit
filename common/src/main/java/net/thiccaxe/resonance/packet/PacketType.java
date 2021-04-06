package net.thiccaxe.resonance.packet;

import net.thiccaxe.resonance.packet.client.auth.AuthenticatedPacket;
import net.thiccaxe.resonance.packet.client.play.ClientUserInfoPacket;
import net.thiccaxe.resonance.packet.server.auth.AuthenticatePacket;
import net.thiccaxe.resonance.packet.server.play.ServerUserInfoPacket;

public enum PacketType {
    INVALID(0x00, "invalid", InvalidPacket::new),
    AUTHENTICATE(0x10, "authenticate", AuthenticatePacket::new),
    AUTHENTICATED(0x11, "authenticated", AuthenticatedPacket::new),
    SERVER_USERINFO(0x20, "user_info", ServerUserInfoPacket::new),
    CLIENT_USERINFO(0x21, "user_info", ClientUserInfoPacket::new);

    public final int id;
    public final String action;
    public final PacketProvider packetProvider;
    PacketType(int id, String action, PacketProvider packetProvider) {
        this.id = id;
        this.action = action;
        this.packetProvider = packetProvider;
    }

    public static PacketType getPacketType(String action) {
        for (PacketType p : PacketType.values()) {
            if (p.action.equalsIgnoreCase(action)) {
                return p;
            }
        }
        return PacketType.INVALID;
    }
    public static Packet getPacket(int id) {
        try {
            for (PacketType p : PacketType.values()) {
                if (p.id == id) {
                    return p.packetProvider.getInstance();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static Packet getPacket(String action) {
        return getPacket(getPacketType(action).id);
    }
    @FunctionalInterface
    public interface PacketProvider {
        Packet getInstance();
    }
}
