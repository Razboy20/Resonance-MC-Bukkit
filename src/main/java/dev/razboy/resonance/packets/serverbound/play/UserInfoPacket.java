package dev.razboy.resonance.packets.serverbound.play;

import dev.razboy.resonance.packets.MalformedPacketException;
import dev.razboy.resonance.packets.PacketType;
import dev.razboy.resonance.packets.serverbound.ServerBoundPacket;
import org.json.JSONObject;

public class UserInfoPacket extends ServerBoundPacket {
    @Override
    protected PacketType setPacketType() {
        return PacketType.USER_INFO;
    }

    private String token;
    public String getToken() {
        return token;
    }


    @Override
    public String repr() {
        return getClass().getSimpleName() + "(token=" + token + ")";
    }

    @Override
    public void readJson(JSONObject json) throws MalformedPacketException {
        if (json.has("bearer")) {
            token = json.get("bearer").toString();
        }
    }
}
