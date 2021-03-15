package dev.razboy.resonance.client;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import dev.razboy.resonance.token.Token;

import java.util.UUID;

public class User {
    private final Token token;
    private boolean online;
    private Player player;

    private JSONObject info;

    public User(Token token, Player player) {
        online = player != null;
        this.player = online ? player : null;
        this.token = token;
        info = new JSONObject().put("pos", new JSONObject()
                .put("x", 0)
                .put("y", 0)
                .put("z", 0)
                .put("rotation", new JSONArray(new float[]{90,0}))
                )
                .put("online", online)
                .put("data", new JSONObject()
                        .put("uuid", token.uuid())
                        .put("username", token.username())
                );
        update();

    }

    public boolean update() {
        try {
            boolean updated;
            JSONObject newInfo = new JSONObject();

            player = Bukkit.getPlayer(UUID.fromString(token.uuid()));
            boolean updatedOnline = player != null;
            updated = updatedOnline != online;

            if (updated) {
                online = updatedOnline;
                newInfo.put("online", online);
            }
            if (online) {
                JSONObject pos = info.getJSONObject("pos");
                JSONObject newPos = new JSONObject();
                JSONArray rot = pos.getJSONArray("rotation");
                JSONArray newRot = new JSONArray();
                Location location = player.getLocation();
                newPos.put("x", location.getBlockX() != pos.getInt("x") ? location.getBlockX() : null);
                newPos.put("y", location.getBlockY() != pos.getInt("y") ? location.getBlockY() : null);
                newPos.put("z", location.getBlockZ() != pos.getInt("z") ? location.getBlockZ() : null);
                newRot.put(0, location.getPitch() != rot.getFloat(0) ? location.getPitch() : rot.getFloat(0));
                newRot.put(1, location.getYaw() != rot.getFloat(1) ? location.getYaw() : rot.getFloat(1));
                newPos.put("rotation", newRot.isEmpty()?null:newRot);
                newInfo.put("pos", newPos.isEmpty()?null:newPos);
                updated = updated || newPos.has("x") || newPos.has("y") || newPos.has("z") || newRot.length() >= 1;
            }
            info = newInfo;
        }
        catch (Exception e) {}
        return false;
    }

    public JSONObject getJson() {
        return info;
    }

    public Player getPlayer() {
        return player;
    }
}
