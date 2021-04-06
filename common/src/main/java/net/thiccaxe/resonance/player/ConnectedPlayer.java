package net.thiccaxe.resonance.player;

import java.util.UUID;

public interface ConnectedPlayer {




    double getX();
    double getY();
    double getZ();
    double getYaw();
    double getPitch();
    UUID getDimension();
    UUID getUuid();
    boolean isOnline();

    Update update();

    Update info();
}
