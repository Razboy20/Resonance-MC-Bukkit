package net.thiccaxe.resonance.packet.server.auth;

import net.thiccaxe.resonance.packet.PacketType;
import net.thiccaxe.resonance.packet.server.ServerBoundPacket;
import org.json.JSONException;
import org.json.JSONObject;

public class AuthenticatePacket extends ServerBoundPacket {
    @Override
    protected PacketType setPacketType() {
        return PacketType.AUTHENTICATE;
    }

    private String authToken;

    public String getAuthToken() {
        return authToken;
    }

    @Override
    public String repr() {
        return getClass().getSimpleName() + "(token=" + authToken + ")";
    }

    @Override
    public void readJson(JSONObject json) throws JSONException {
        JSONObject body = json.getJSONObject("body");
        authToken = body.get("token").toString();
    }
}
