package dev.razboy.resonance.packets.clientbound.play;

import dev.razboy.resonance.packets.PacketType;
import dev.razboy.resonance.packets.clientbound.ClientBoundPacket;
import org.json.JSONObject;

public class OUserInfoPacket extends ClientBoundPacket {
    public final static PacketType id = PacketType.USER_INFO;
    public final static String action = id.action;


    private String token;
    private JSONObject user;
    public void setToken(String token) {
        this.token = token;
    }
    public void setUser(JSONObject user) {
        this.user = user;
    }

    @Override
    public String read() {
        return withId().put("action", action)
                .put("body", new JSONObject()
                        .put("user", user)
                        .put("token", token)
                ).toString();
    }
}
