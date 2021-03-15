package dev.razboy.resonance.packets.serverbound.auth;

import dev.razboy.resonance.packets.MalformedPacketException;
import dev.razboy.resonance.packets.PacketType;
import dev.razboy.resonance.packets.serverbound.ServerBoundPacket;
import org.json.JSONObject;

public class AuthTokenAuthenticatePacket extends ServerBoundPacket {
    public final static PacketType id = PacketType.AUTHENTICATE;
    public final static String action = "authenticate";

    private String authToken;

    public String getAuthToken() {
        return authToken;
    }

    @Override
    public String read() {
        return null;
    }

    @Override
    public void readJson(JSONObject json) throws MalformedPacketException {
        if (json.has("body")) {
            JSONObject body = json.getJSONObject("body");
            if (body.has("token")) {
                authToken = body.get("token").toString();
            }
        }
    }
}
