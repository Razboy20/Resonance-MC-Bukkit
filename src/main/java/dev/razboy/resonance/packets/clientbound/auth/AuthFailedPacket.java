package dev.razboy.resonance.packets.clientbound.auth;

import dev.razboy.resonance.packets.PacketType;
import dev.razboy.resonance.packets.clientbound.ClientBoundPacket;
import org.json.JSONObject;

public class AuthFailedPacket extends ClientBoundPacket {
    public final static PacketType id = PacketType.AUTH_FAILED;
    public final static String action = id.action;

    @Override
    public String read() {
        return withId().put("action", action).toString();
    }
}
