package net.thiccaxe.resonance.packet.server;

import net.thiccaxe.resonance.packet.Packet;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class ServerBoundPacket extends Packet {
    public boolean isServerBound() {
        return true;
    }

    public boolean isClientBound() {
        return false;
    }

    @Override
    public String read() {
        return null;
    }

    public abstract void readJson(JSONObject json) throws JSONException;
}