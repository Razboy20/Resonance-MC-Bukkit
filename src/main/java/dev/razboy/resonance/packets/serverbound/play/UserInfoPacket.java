package dev.razboy.resonance.packets.serverbound.play;

import dev.razboy.resonance.packets.MalformedPacketException;
import dev.razboy.resonance.packets.PacketType;
import dev.razboy.resonance.packets.serverbound.ServerBoundPacket;
import org.json.JSONObject;

public class UserInfoPacket extends ServerBoundPacket {
    public final static PacketType id = PacketType.AUTHENTICATE;
    public final static String action = "authenticate";

    private String token;
    public String getToken() {
        return token;
    }

    @Override
    public void readJson(JSONObject json) throws MalformedPacketException {
        if (json.has("bearer")) {
            token = json.get("bearer").toString();
        }
    }
}
