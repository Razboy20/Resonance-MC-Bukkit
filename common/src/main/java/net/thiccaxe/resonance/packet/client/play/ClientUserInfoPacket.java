package net.thiccaxe.resonance.packet.client.play;

import net.thiccaxe.resonance.packet.PacketType;
import net.thiccaxe.resonance.packet.client.ClientBoundPacket;
import org.json.JSONObject;

import java.util.UUID;

public class ClientUserInfoPacket extends ClientBoundPacket {
    @Override
    protected PacketType setPacketType() {
        return PacketType.CLIENT_USERINFO;
    }

    private String token;
    private JSONObject user =  new JSONObject()
                    .put("data", new JSONObject()
                            .put("uuid", UUID.randomUUID().toString())
                            .put("username", "abcdef")
                    )
                    .put("online", false);

    public void setToken(String token) {
        this.token = token;
    }
    public void setUser(JSONObject user) {
        this.user = user;
    }

    @Override
    public String repr() {
        return getClass().getSimpleName() + "(token=" + token + ", user=" + user.toString() + ")";
    }


    @Override
    public String read() {
        return withIdActionBody(new JSONObject()
                //put("token", token)
                .put("user", user)
        ).toString();
    }
}
