package net.thiccaxe.resonance.packet.client.auth;


import io.netty.handler.codec.json.JsonObjectDecoder;
import net.thiccaxe.resonance.packet.PacketType;
import net.thiccaxe.resonance.packet.client.ClientBoundPacket;
import org.json.JSONObject;

import java.util.UUID;

public class AuthenticatedPacket extends ClientBoundPacket {
    @Override
    protected PacketType setPacketType() {
        return PacketType.AUTHENTICATED;
    }


    private String token;
    private JSONObject user =  new JSONObject()
            .put("data", new JSONObject()
                    .put("uuid", UUID.randomUUID().toString())
                    .put("username", "abcdef")
            )
            .put("online", false);



    @Override
    public String read() {
        return withIdActionBody(new JSONObject()
                .put("token", token)
                .put("user", user)
        ).toString();
    }

    @Override
    public String repr() {
        return getClass().getSimpleName() + "(token=" + token + ")";
    }

    public void setToken(String token) {
        this.token = token;
    }
}
