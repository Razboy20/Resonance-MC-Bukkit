package dev.razboy.resonance.listener;

import dev.razboy.resonance.request.WebSocketManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MessageListener implements Listener {
    private final WebSocketManager webSocketManager;

    public MessageListener(WebSocketManager webSocketManager) {
        this.webSocketManager = webSocketManager;
    }

    @EventHandler
    public void onMessage(AsyncChatEvent event) {
        webSocketManager.send(event.message());
    }
}
