package dev.razboy.resonance.client;

import dev.razboy.resonance.token.Token;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

public class User {
    private final Token token;
    private boolean online;
    private Player player;

    private final JSONObject info;

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
        boolean updated;
        player = Bukkit.getPlayer(UUID.fromString(token.uuid()));
        boolean updatedOnline = player != null;
        updated = updatedOnline != online;
        info.put("online", online);
        if (online) {
            JSONObject pos = info.getJSONObject("pos");
            Location location = player.getLocation();
            updated = (pos.getJSONArray("rotation").getFloat(0) != location.getYaw())||(pos.getJSONArray("rotation").getFloat(1) != location.getPitch())||(pos.getInt("x") != location.getBlockX())||(pos.getInt("y") != location.getBlockY())||(pos.getInt("z") != location.getBlockZ());;

            pos.put("x", location.getBlockX());
            pos.put("y", location.getBlockY());
            pos.put("z", location.getBlockZ());

            pos.put("rotation", new JSONArray(new float[]{location.getYaw(), location.getPitch()}));
        }
        return updated;
    }

    public JSONObject getJson() {
        return info;
    }

    public Player getPlayer() {
        return player;
    }
}
