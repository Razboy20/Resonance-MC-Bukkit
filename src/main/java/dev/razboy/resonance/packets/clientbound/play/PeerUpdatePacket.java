package dev.razboy.resonance.packets.clientbound.play;

import dev.razboy.resonance.packets.PacketType;
import dev.razboy.resonance.packets.clientbound.ClientBoundPacket;
import org.json.JSONArray;
import org.json.JSONObject;

public class PeerUpdatePacket extends ClientBoundPacket {
    @Override
    protected PacketType setPacketType() {
        return PacketType.PEER_UPDATE;
    }


    private JSONArray peers;

    public void addPeer(JSONObject peer) {
        peers.put(peer);
    }

    @Override
    public String read() {
        return withIdActionBody(new JSONObject().put("peers", peers)).toString();
    }
}
