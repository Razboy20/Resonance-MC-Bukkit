package net.thiccaxe.resonance.packet.server;

import net.thiccaxe.resonance.player.ConnectedPlayer;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class NeedsAuthPacket extends ServerBoundPacket {
    private ConnectedPlayer player;

    protected abstract void readAuthedJson(JSONObject json);

    @Override
    public void readJson(JSONObject json) throws JSONException {
        if (json.has("bearer")) {
            String bearer =  json.get("bearer").toString();
            player = null;
            readAuthedJson(json);
        }
    }
}
