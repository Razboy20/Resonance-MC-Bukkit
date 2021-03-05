package dev.razboy.resonance.request;

import dev.razboy.resonance.Resonance;
import dev.razboy.resonance.network.Request;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class IRequestManager implements Runnable {
    protected boolean looping = true;
    protected static Resonance plugin;
    protected ConcurrentLinkedQueue<Request> queue = new ConcurrentLinkedQueue<>();

    protected abstract void handle(Request request);
    protected abstract void additional();

    public void stop() {
        looping = false;
    }


    public void add(Request request) {
        queue.add(Objects.requireNonNull(request));
    }

    @Override
    public void run() {
        if (looping) {
            for (Request request : queue) {
                if (request != null) {
                    handle(request);
                }
            }
            queue.clear();
            additional();
        }
    }
}
