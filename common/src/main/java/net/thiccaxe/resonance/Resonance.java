package net.thiccaxe.resonance;

import net.thiccaxe.resonance.server.ResonanceServer;
import net.thiccaxe.resonance.server.request.AsyncNetworkHandler;
import net.thiccaxe.resonance.server.request.NetworkMessage;
import net.thiccaxe.resonance.server.request.SyncNetworkHandler;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public interface Resonance {
    ResonanceProvider provider = new ResonanceProvider();

    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

    ResonanceServer resonanceServer = new ResonanceServer();
    SyncNetworkHandler syncNetworkHandler = new SyncNetworkHandler();
    AsyncNetworkHandler asyncNetworkHandler = new AsyncNetworkHandler();

    default void startResonanceServer(ResonanceServer server, AsyncNetworkHandler asyncNetworkHandler, SyncNetworkHandler syncNetworkHandler) {
        scheduledExecutorService.scheduleAtFixedRate(asyncNetworkHandler, 0, 50, TimeUnit.MILLISECONDS);
        scheduledExecutorService.submit(server);
        asyncNetworkHandler.start();
        startSyncNetworkHandler(syncNetworkHandler);
    }

    void startSyncNetworkHandler(SyncNetworkHandler syncNetworkHandler);

    default void init(ServerType serverType, int port, Resonance i) {
        provider.setInstance(i);
        resonanceServer.setPort(port);
        System.out.println("Resonance is starting under: " + serverType.name());
        System.out.println("Loading Config...");
        System.out.println("Starting Resonance Server...");
        startResonanceServer(resonanceServer, asyncNetworkHandler, syncNetworkHandler);
        System.out.println("Started Resonance Server!");
    }

    default void shutdown() {
        resonanceServer.stop();
        asyncNetworkHandler.stop();
        scheduledExecutorService.shutdownNow();
        stopSyncNetworkHandler();
    }

    void stopSyncNetworkHandler();

    File getFolder();

    File getWebsiteFolder();

    default void addIncoming(NetworkMessage message) {
        syncNetworkHandler.addIncoming(message);
    }
}
