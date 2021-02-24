package dev.razboy.resonance.commands;

import dev.razboy.resonance.Resonance;
import dev.razboy.resonance.token.AuthToken;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VoiceConnectCommand implements CommandExecutor {
    private static final Component errorMessage = MiniMessage.get().parse("<dark_red><bold>Error!<reset><gray>Could not create a token!");
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Audience audience = Resonance.getAdventure().sender(sender);
            Player player = ((Player) sender);
            Resonance.getTokenManager().generateAuthToken(player.getUniqueId().toString(), player.getName());
            AuthToken token = Resonance.getTokenManager().getAuthToken(player.getUniqueId().toString());
            if (token != null) {
                Component message = Component.text(token.toString(), TextColor.color(0x444444));
                audience.sendMessage(message);
                return true;
            }
            audience.sendMessage(errorMessage);
            return false;
        }
        return false;
    }
}
