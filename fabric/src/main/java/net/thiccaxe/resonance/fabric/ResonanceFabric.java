package net.thiccaxe.resonance.fabric;

import net.fabricmc.api.ModInitializer;
import net.thiccaxe.resonance.Resonance;
import net.thiccaxe.resonance.ServerType;
import net.thiccaxe.resonance.server.ResonanceServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.LogManager;

public class ResonanceFabric implements ModInitializer, Resonance {


    @Override
    public void onInitialize() {
        init(ServerType.FABRIC, 25560);
    }

    @Override
    public void startResonanceServer(ResonanceServer server) {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        
    }
}
