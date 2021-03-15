package dev.razboy.resonance.packets;

import dev.razboy.resonance.packets.clientbound.auth.AuthFailedPacket;
import dev.razboy.resonance.packets.clientbound.auth.AuthenticatedPacket;
import dev.razboy.resonance.packets.clientbound.play.UserUpdatePacket;
import dev.razboy.resonance.packets.serverbound.auth.AuthTokenAuthenticatePacket;
import dev.razboy.resonance.packets.serverbound.auth.LogoutPacket;
import dev.razboy.resonance.packets.serverbound.play.UserInfoPacket;

public enum PacketType {
    INVALID(0x00, "invalid"),
    AUTHENTICATE(0x01, "authenticate"),
    AUTHENTICATED(0x02, "authenticated"),
    USER_INFO(0x04, "user_info"),
    USER_UPDATE(0x05, "user_update"),
    LOGOUT(0xFF, "logout"),
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
            case 0x02:
                return new AuthenticatedPacket();
            case 0x03:
                return new AuthFailedPacket();
            case 0x04:
                return new UserInfoPacket();
            case 0x05:
                return new UserUpdatePacket();
            case 0xFF:
                return new LogoutPacket();
        }
    }
    public static Packet getPacket(String action) {
        return getPacket(getPacketType(action).id);
    }
}
