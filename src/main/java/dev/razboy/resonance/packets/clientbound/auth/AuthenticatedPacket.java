package dev.razboy.resonance.packets.clientbound.auth;

import dev.razboy.resonance.packets.PacketType;
import dev.razboy.resonance.packets.clientbound.ClientBoundPacket;
import org.json.JSONObject;

public class AuthenticatedPacket extends ClientBoundPacket {
    public final static PacketType id = PacketType.AUTHENTICATE;
    public final static String action = id.action;


    private String token;
    private JSONObject user;

    @Override
    public String read() {
        return new JSONObject().put("action", action)
                .put("body", new JSONObject()
                        .put("user", user)
                        .put("token", token)
                ).toString();
    }

    public void setToken(String token) {
        this.token = token;
    }
    public void setUser(JSONObject user) {
        this.user = user;
    }
}
