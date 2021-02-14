package dev.razboy.resonance.commands;

import dev.razboy.resonance.Resonance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BroadcastCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length >= 1){
            Resonance.getRequestManager().broadcast(args[0]);
            return true;
        }
        return false;
    }
}
