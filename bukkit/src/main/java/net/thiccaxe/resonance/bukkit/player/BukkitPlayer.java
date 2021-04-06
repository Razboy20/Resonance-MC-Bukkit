package net.thiccaxe.resonance.bukkit.player;

import net.thiccaxe.resonance.player.ConnectedPlayer;
import net.thiccaxe.resonance.player.Update;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class BukkitPlayer implements ConnectedPlayer {
    private final UUID uuid;
    private Player player;

    private double x = 0d;
    private double y = 0d;
    private double z = 0d;
    private float yaw = 0f;
    private float pitch = 0f;
    private UUID dimension = UUID.randomUUID();
    private boolean online = false;



    public BukkitPlayer(UUID uuid) {
        this.uuid = uuid;
        player = Bukkit.getPlayer(uuid);
        update();
    }

    @Override
    public double getX() {
        return 0;
    }

    @Override
    public double getY() {
        return 0;
    }

    @Override
    public double getZ() {
        return 0;
    }

    @Override
    public double getYaw() {
        return 0;
    }

    @Override
    public double getPitch() {
        return 0;
    }

    @Override
    public UUID getDimension() {
        return null;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public boolean isOnline() {
        return online;
    }

    @Override
    public Update update() {
        Update update = new Update();
        player = Bukkit.getPlayer(uuid);
        online = update.setOnline(online, player!=null);
        if (online) {
            Location location = player.getLocation();
            x = update.setX(x, location.getX());
            y = update.setY(y, location.getY());
            z = update.setZ(z, location.getZ());
            yaw = update.setYaw(yaw, location.getYaw());
            pitch = update.setYaw(pitch, location.getPitch());
            World world = location.getWorld();
            if (world != null) {
                dimension = update.setDimension(dimension, world.getUID());
            }
        }
        return update;
    }

    @Override
    public Update info() {
        Update update = new Update();
        player = Bukkit.getPlayer(uuid);
        online = update.setOnline(online, player!=null);
        update.setX(x, x);
        update.setX(y, y);
        update.setX(z, z);
        update.setYaw(yaw, yaw);
        update.setYaw(pitch, pitch);
        dimension = update.setDimension(dimension, dimension);
        return update;
    }
}
