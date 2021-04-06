package net.thiccaxe.resonance.packet.server.play;

import net.thiccaxe.resonance.packet.PacketType;
import net.thiccaxe.resonance.packet.server.ServerBoundPacket;
import org.json.JSONException;
import org.json.JSONObject;

public class ServerUserInfoPacket extends ServerBoundPacket {
    @Override
    protected PacketType setPacketType() {
        return PacketType.SERVER_USERINFO;
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
    public void readJson(JSONObject json) throws JSONException {
        try {
            token = json.getString("bearer");
        } catch (JSONException ignored) {}
    }
}
