package dev.bodner.jack.lux.commands;

import dev.bodner.jack.lux.Lux;
import dev.bodner.jack.lux.util.JsonUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class SetPVPCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()){
            if (args.length != 2){
                sender.sendMessage("Invalid argument, must have two arguments");
                return false;
            }
            UUID id = JsonUtil.getPlayerID(args[1]);
            if (id == null){
                sender.sendMessage("Player not found");
                return false;
            }
            switch (args[0]){
                case "on":
                    sender.sendMessage(args[1]+"'s PVP is now enabled");
                    Lux.noPVP.remove(id);
                    return true;
                case "off":
                    sender.sendMessage(args[1]+"'s PVP is now disabled");
                    if (!Lux.noPVP.contains(id)){
                        Lux.noPVP.add(id);
                    }
                    return true;
                default:
                    sender.sendMessage("Invalid argument, first argument must be either \"on\" or \"off\".");
                    return false;
            }
        }
        else {
            sender.sendMessage("You do not have permission to use this command");
        }
        return false;
    }
}
