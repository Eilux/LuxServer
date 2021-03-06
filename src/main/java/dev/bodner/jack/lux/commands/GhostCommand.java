package dev.bodner.jack.lux.commands;

import dev.bodner.jack.lux.Lux;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GhostCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player && sender.isOp()){
            Player player = (Player)sender;
            if(Lux.ghostLocation.containsKey(player.getUniqueId())){
                player.teleport(Lux.ghostLocation.get(player.getUniqueId()));
                Bukkit.broadcastMessage(String.format("§e%s joined the game", player.getDisplayName()));
                player.setGameMode(GameMode.CREATIVE);
                Lux.ghostLocation.remove(player.getUniqueId());
            }
            else {
                Bukkit.broadcastMessage(String.format("§e%s left the game", player.getDisplayName()));
                Lux.ghostLocation.put(player.getUniqueId(),player.getLocation());
                player.setGameMode(GameMode.SPECTATOR);
            }
            return true;
        }
        return false;
    }
}
