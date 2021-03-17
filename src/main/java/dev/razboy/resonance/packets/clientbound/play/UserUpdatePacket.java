package dev.razboy.resonance.packets.clientbound.play;

import dev.razboy.resonance.packets.PacketType;
import dev.razboy.resonance.packets.clientbound.ClientBoundPacket;
import org.json.JSONObject;

public class UserUpdatePacket extends ClientBoundPacket {
    @Override
    protected PacketType setPacketType() {
        return PacketType.USER_UPDATE;
    }

    private JSONObject user;

    public void setUser(JSONObject user) {
        this.user = user;
    }

    @Override
    public String repr() {
        return getClass().getSimpleName() + "(user=" + user.toString() + ")";
    }

    @Override
    public String read() {
        return withIdActionBody(new JSONObject().put("type", "position").put("pos", user.getJSONObject("pos"))).toString();
    }
}
