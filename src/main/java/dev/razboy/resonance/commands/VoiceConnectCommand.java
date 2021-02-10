package dev.razboy.resonance.commands;

import dev.razboy.resonance.Resonance;
import dev.razboy.resonance.server.structs.tokens.Token;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VoiceConnectCommand implements CommandExecutor {
    LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        Token token = Resonance.getInstance().getTokenManager().generateToken(player);
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
        ComponentBuilder header = Component.text()
                .color(TextColor.color(0x9f9f9f))
                .content("A new token has been generated! It will be valid for 5 minutes.\n");
        ComponentBuilder clipboard = Component.text()
                .color(TextColor.color(0xF53900))
                .decorate(TextDecoration.BOLD)
                .content("[COPY]")
                .hoverEvent(HoverEvent.showText(Component.text().color(TextColor.color(0xF53900)).content("Click to copy to clipboard!")));
        ComponentBuilder middle = Component.text()
                .color(TextColor.color(0x9f9f9f))
                .content(", or ");
        ComponentBuilder url = Component.text()
                .color(TextColor.color(0xF53900))
                .decorate(TextDecoration.BOLD)
                .content("[LINK]")
                .hoverEvent(HoverEvent.showText(Component.text().color(TextColor.color(0xF53900)).content("Click to open the link!")));
        Component message = header.append(clipboard.clickEvent(ClickEvent.copyToClipboard(token.getToken())).build()).append(middle.build()).append(url.clickEvent(ClickEvent.openUrl("https://thiccaxe.net/token?token=" + token)).build()).build();
        Resonance.getAdventure().sender(sender).sendMessage(message);
        return true;
    }
}
