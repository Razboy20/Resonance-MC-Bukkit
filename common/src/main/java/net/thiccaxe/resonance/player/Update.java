package net.thiccaxe.resonance.player;

import java.util.UUID;

public class Update {
    private Double x = null;
    private Double y = null;
    private Double z = null;
    private Float yaw = null;
    private Float pitch = null;
    private UUID dimension = null;
    private Boolean online = null;


    public Update(){}

    public double setX(double o, double n) {
        if (o != n) {
            x = n;
        }
        return n;

    }
    public double setY(double o, double n) {
        if (o != n) {
            y = n;
        }
        return n;

    }
    public double setZ(double o, double n) {
        if (o != n) {
            z = n;
        }
        return n;

    }
    public float setYaw(float o, float n) {
        if (o != n) {
            yaw = n;
        }
        return n;

    }
    public double setPitch(float o, float n) {
        if (o != n) {
            pitch = n;
        }
        return n;

    }


    public boolean setOnline(boolean o, boolean n) {
        if (o != n) {
            online = o;
        }
        return n;

    }

    public UUID setDimension(UUID o, UUID n) {
        if (o != n) {
            dimension = o;
        }
        return n;
    }
}
