package dev.razboy.resonance.command;

import dev.razboy.resonance.Resonance;
import dev.razboy.resonance.token.Token;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TokenCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {return false;}
        Player player = (Player) sender;
        if (args.length < 1) {
            Token token = Resonance.getTokenManager().generateAuthToken(player);
            player.sendMessage(token.token() + ":\n" + token.username() + " : " + token.uuid());
            return true;
        }
        if (args[0].equalsIgnoreCase("l")) {
            player.sendMessage(Resonance.getTokenManager().listTokens());
            return true;
        } else if (args[0].equalsIgnoreCase("c")) {
            Token token = new Token(player.getUniqueId().toString(), player.getName(), true);
            Resonance.getTokenManager().registerToken(token);
            player.sendMessage(token.token() + ":\n" + token.username() + " : " + token.uuid());
        }
        return false;


    }
}
