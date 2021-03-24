package net.thiccaxe.resonance;

import net.thiccaxe.resonance.config.Configuration;
import net.thiccaxe.resonance.server.ResonanceServer;

public interface Resonance {
    ResonanceServer resonanceServer = new ResonanceServer();

    void startResonanceServer(ResonanceServer resonanceServer);

    default void init(ServerType serverType, int port) {
        resonanceServer.setPort(port);
        System.out.println("Resonance is starting under: " + serverType.name());
        System.out.println("Loading Config...");
        System.out.println("Starting Resonance Server...");
        startResonanceServer();
        System.out.println("Started Resonance Server!");
    }


    default void shutdown() {
        resonanceServer.stop();
    }
}
