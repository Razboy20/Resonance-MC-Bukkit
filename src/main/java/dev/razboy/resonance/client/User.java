package dev.razboy.resonance.client;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import dev.razboy.resonance.token.Token;

import java.util.Objects;
import java.util.UUID;

public class User {
    private final Token token;
    private boolean online;
    private Player player;

    private JSONObject playerData;

    private double posX = 0.00d;
    private double posY = 0.00d;
    private double posZ = 0.00d;
    private double rotYaw = 90.00d;
    private double rotPitch = 0.00d;
    private double uposX = 0.00d;
    private double uposY = 0.00d;
    private double uposZ = 0.00d;
    private double urotYaw = 90.00d;
    private double urotPitch = 0.00d;

    private JSONObject info;
    public User(Token token, Player player) {
        online = player != null;
        this.player = online ? player : null;
        this.token = token;
        this.playerData = new JSONObject().put("uuid", token.uuid()).put("username", token.username());
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
        if (onlineUpdate()) {updateOnline();}
        if (positionUpdate()) {updatePosition();}

    }

    public JSONObject update() {
        try {
            player = Bukkit.getPlayer(UUID.fromString(token.uuid()));
            online = player != null;
            if (online) {
                JSONObject newInfo = withData().put("online", online);
                Location location = player.getLocation();
                JSONObject pos = new JSONObject();
                    pos.put("x", round(location.getX()));
                    pos.put("y", round(location.getY()));
                    pos.put("z", round(location.getZ()));
                    pos.put("rotation", new JSONArray(new double[]{round(location.getYaw()), round(location.getPitch())}));
                newInfo.put("pos", pos);
                info = newInfo;
            }
        }
        catch (Exception ignored) {}
        return info;
    }

    private double round(double n) {
        return Math.round(n*100.0)/100.0;
    }

    public JSONObject getInitialJson() {
        return update();
    }

    public Player getPlayer() {
        return player;
    }

    public boolean onlineUpdate() {
        return online == (Bukkit.getPlayer(UUID.fromString(token.uuid())) == null);
    }
    public JSONObject updateOnline() {
        online = Bukkit.getPlayer(UUID.fromString(token.uuid())) != null;
        return new JSONObject()
                .put("user", withData()
                        .put("online", online)
                );
    }

    private JSONObject withData() {
        return new JSONObject().put("data", playerData);
    }

    public boolean positionUpdate() {
        if (online) {
            Location l = Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(token.uuid()))).getLocation();
            uposX = round(l.getX());
            uposY = round(l.getY());
            uposZ = round(l.getZ());
            urotYaw = round(l.getYaw());
            urotPitch = round(l.getPitch());
            return uposX != posX || uposY != posY || uposZ != posZ || urotPitch != rotPitch || urotYaw != rotYaw;
        }
        return false;
    }
    public JSONObject updatePosition() {
        boolean xupdated = uposX != posX;
        boolean yupdated = uposY != posY;
        boolean zupdated = uposZ != posZ;
        boolean rupdated = urotPitch != rotPitch || urotYaw != rotYaw;
        if (xupdated) {
            posX = uposX;
        }
        if (yupdated) {
            posY = uposY;
        }
        if (zupdated) {
            posZ = uposZ;
        }
        if (rupdated) {
            rotPitch = urotPitch;
            rotYaw = urotYaw;
        }
        return new JSONObject()
                .put("user", withData()
                        .put("pos", new JSONObject()
                                .put("x", xupdated ? posX:null)
                                .put("y", yupdated ? posY:null)
                                .put("z", zupdated ? posZ:null)
                                .put("rotation", rupdated ? new JSONArray(new double[]{rotYaw, rotPitch}):null)
                        )
                );
    }
}
