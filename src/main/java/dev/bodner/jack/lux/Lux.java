package dev.bodner.jack.lux;

import dev.bodner.jack.lux.commands.GhostCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class Lux extends JavaPlugin {
    public static HashMap<UUID, Location> ghostLocation = new HashMap<>();
    public static ArrayList<Player> ghosts = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.saveDefaultConfig();

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()){
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    player.showPlayer(Lux.this, player1);
                }
                for (Player ghost : ghosts){
                    player.hidePlayer(Lux.this, ghost);
                }
            }
        }, 0L, 1L);

        this.getCommand("ghost").setExecutor(new GhostCommand());
    }

    @Override
    public void onDisable() {
//        ghostLocation.forEach((uuid,location) -> {
//            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
//            player.getPlayer().teleport(location);
//        });
    }
}
