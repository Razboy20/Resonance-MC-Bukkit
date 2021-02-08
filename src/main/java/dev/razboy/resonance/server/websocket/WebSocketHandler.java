package dev.razboy.resonance.server.websocket;

import com.corundumstudio.socketio.*;
import dev.razboy.resonance.server.structs.RTCIceCandidate;

public class WebSocketHandler {
    private static SocketIOServer server = null;

    // Test Method
    public static void main(String[] args) {
        open();
    }

    public static void open() {

        assert server == null;
        Configuration config = new Configuration();
        config.setPort(25560);
        config.getSocketConfig().setReuseAddress(true);

        server = new SocketIOServer(config);
        registerEvents();
        server.start();
    }

    private static void registerEvents() {
        assert server != null;
        server.addEventListener("relayIceCandidate", RTCIceCandidate.class, (client, data, ackRequest) -> {
            // broadcast messages to all clients
            server.getBroadcastOperations().sendEvent("relayIceCandidate", data);
        });

        server.addEventListener("locationInfo", String.class, (client, data, ackRequest) -> {
            // broadcast messages to all clients
            server.getBroadcastOperations().sendEvent("locationInfo", data);
        });
    }

    public static void disconnect() {
        assert server != null;
        server.stop();
    }
}