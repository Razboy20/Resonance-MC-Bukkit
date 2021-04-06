package net.thiccaxe.resonance.server.request;

import net.thiccaxe.resonance.Resonance;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class NetworkHandler implements Runnable {
    protected boolean running = false;
    protected static Resonance plugin;
    protected ConcurrentLinkedQueue<NetworkMessage> incomingQueue = new ConcurrentLinkedQueue<>();
    protected ConcurrentLinkedQueue<NetworkMessage> outgoingQueue = new ConcurrentLinkedQueue<>();

    protected abstract void handleIncoming(NetworkMessage request);
    protected abstract void handleOutgoing(NetworkMessage response);

    public void stop() {running = false;}
    public void start() {running = true;}

    public void addIncoming(NetworkMessage request) {
        if (request != null && request.getPacket() != null) {
            incomingQueue.add(request);
        }
    }
    public void addOutgoing(NetworkMessage response) {
        outgoingQueue.add(Objects.requireNonNull(response));
    }

    @Override
    public void run() {
        try {
            if (running) {
                if (!incomingQueue.isEmpty()) {
                    for (NetworkMessage request : incomingQueue) {
                        if (request != null) {
                            handleIncoming(request);
                        }
                    }
                    incomingQueue.clear();
                }
                if (!outgoingQueue.isEmpty()) {
                    for (NetworkMessage response : outgoingQueue) {
                        if (response != null) {
                            handleOutgoing(response);
                        }
                    }
                    outgoingQueue.clear();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            stop();


        }
    }

}
