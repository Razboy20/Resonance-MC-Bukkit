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
                .put("x", 0.00d)
                .put("y", 0.00d)
                .put("z", 0.00d)
                .put("rotation", new JSONArray(new double[]{90.00d,0.00d}))
                )
                .put("online", online)
                .put("data", new JSONObject()
                        .put("uuid", token.uuid())
                        .put("username", token.username())
                );
        update();

    }

    public JSONObject update() {
        try {
            boolean updated;
            JSONObject newInfo = new JSONObject();

            player = Bukkit.getPlayer(UUID.fromString(token.uuid()));
            boolean online = player != null;
            newInfo.put("online", online); info.put("online", online);
            if (online) {
                Location location = player.getLocation();
                JSONObject pos = new JSONObject(info.getJSONObject("pos"), JSONObject.getNames(info.getJSONObject("pos")));
                if (pos.getDouble("x") != round(location.getX())) {
                    pos.put("x", round(location.getX()));
                }
                if (pos.getDouble("y") != round(location.getY())) {
                    pos.put("y", round(location.getY()));
                }
                if (pos.getDouble("z") != round(location.getZ())) {
                    pos.put("z", round(location.getZ()));
                }
            if (pos.getJSONArray("rotation").getDouble(0) != round(location.getYaw()) || pos.getJSONArray("rotation").getDouble(1) != round(location.getPitch())) {
                pos.put("rotation", new JSONArray(new double[]{round(location.getYaw()), round(location.getPitch())}));
            }
            newInfo.put("pos", pos);
            }
            info = newInfo;
        }
        catch (Exception e) {}
        return info;
    }
    private double round(double n) {
        return Math.round(n*100.0)/100.0;
    }

    public JSONObject getJson() {
        return info;
    }

    public Player getPlayer() {
        return player;
    }
}
