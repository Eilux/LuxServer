package dev.bodner.jack.lux.commands;

import dev.bodner.jack.lux.Lux;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PVPCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player)sender;
            if (Lux.noPVP.contains(player.getUniqueId())){
                sender.sendMessage("PVP Enabled");
                Lux.noPVP.remove(player.getUniqueId());

            }
            else {
                sender.sendMessage("PVP Disabled");
                Lux.noPVP.add(player.getUniqueId());
            }
            return true;
        }
        return false;
    }
}
