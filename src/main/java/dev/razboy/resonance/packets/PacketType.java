package dev.razboy.resonance.packets;

import dev.razboy.resonance.packets.serverbound.auth.AuthTokenAuthenticatePacket;

public enum PacketType {
    INVALID(0x00, "invalid"),
    AUTHENTICATE(0x01, "authenticate"),
    AUTHENTICATED(0x02, "authenticated"),
    AUTH_FAILED(0x03, "auth_failed");
    public final int id;
    public final String action;
    PacketType(int id, String action) {
        this.id = id;
        this.action = action;
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
        switch (id) {
            default:
            case 0x00:
                return null;
            case 0x01:
                return new AuthTokenAuthenticatePacket();
        }
    }
    public static Packet getPacket(String action) {
        return getPacket(getPacketType(action).id);
    }
}
