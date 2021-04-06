package net.thiccaxe.resonance.player;

import net.thiccaxe.resonance.player.ConnectedPlayer;
import net.thiccaxe.resonance.server.Connection;

public class ConnectionPlayerBridge {
    private final ConnectedPlayer player;
    private final Connection connection;

    public ConnectionPlayerBridge(ConnectedPlayer player, Connection connection) {
        this.player = player;
        this.connection = connection;
    }

    public ConnectedPlayer getPlayer() {
        return player;
    }
    public Connection getConnection() {
        return connection;
    }

}
