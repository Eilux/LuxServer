package dev.bodner.jack.lux;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.bodner.jack.lux.commands.GhostCommand;
import dev.bodner.jack.lux.commands.PVPCommand;
import dev.bodner.jack.lux.commands.SetPVPCommand;
import dev.bodner.jack.lux.json.PVPData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class Lux extends JavaPlugin implements Listener {
    public static HashMap<UUID, Location> ghostLocation = new HashMap<>();
    String base_path = getDataFolder().getAbsolutePath()+File.separator+"data";
    private FileWriter fileWriter;
    public static ArrayList<UUID> noPVP;
    File basePath = new File(base_path);
    File pvpPath = new File(base_path+File.separator+"pvp_data.json");


    @Override
    public void onEnable() {

        //config files and related nonsense
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        JsonParser parser = new JsonParser();


        this.saveDefaultConfig();

        try {
            if (!basePath.exists()){
                System.out.println("[Lux] Creating data folder...");
                boolean create = basePath.mkdir();
                if (create){
                    System.out.println("[Lux] Data folder created at: " + base_path);
                }
            }
            if (!pvpPath.exists()){
                System.out.println("[Lux] Creating pvp data file...");
                pvpPath.createNewFile();
                PVPData blank = new PVPData();
                BufferedWriter writer = new BufferedWriter(new FileWriter(pvpPath));
                System.out.println(gson.toJson(blank));
                writer.write(gson.toJson(blank));
                writer.close();
            }

            try{
                FileInputStream stream = new FileInputStream(pvpPath);
                JsonObject object = (JsonObject)parser.parse(new InputStreamReader(stream));
                PVPData data = gson.fromJson(object, PVPData.class);
                noPVP = data.format();
            }
            catch (Exception e){
                System.out.println("issue with pvp list creating empty array");
                noPVP = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //event for ghost
        getServer().getPluginManager().registerEvents(this, this);
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()){
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    player.showPlayer(Lux.this, player1);
                }
                ghostLocation.forEach(((uuid, location) -> {
                    Player ghost = Bukkit.getPlayer(uuid);
                    if (ghost != null){
                        player.hidePlayer(Lux.this, ghost);
                        if (ghost.getGameMode() != GameMode.SPECTATOR){
                            ghost.setGameMode(GameMode.SPECTATOR);
                        }
                    }
                }));
            }
        }, 0L, 1L);

//        scheduler.scheduleSyncRepeatingTask(this, () -> {
//            for (Player player : Bukkit.getOnlinePlayers()){
//                if (!noPVP.contains(player.getUniqueId())){
//                    player.setDisplayName("§4\uD83D\uDDE1§r " + player.getDisplayName());
//                }
//            }
//
//        }, 0L, 1L);



        //command executors
        this.getCommand("ghost").setExecutor(new GhostCommand());
        this.getCommand("pvp").setExecutor(new PVPCommand());
        this.getCommand("setpvp").setExecutor(new SetPVPCommand());
    }

    private boolean canPlayerHurt(Player target){
        return noPVP.contains(target.getUniqueId());
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e){
        //I kind of "borrowed" this part from this post: https://bukkit.org/threads/stopping-players-from-damaging-other-players-in-same-array.125550/
        if(!(e.getEntity() instanceof Player)) {
            // Victim is not a player
            return;
        }

        // Cast victim
        Player victim = (Player) e.getEntity();

        // Create an empty player object to store attacker
        Player attacker = null;

        if(e.getDamager() instanceof Player) {
            // Attacker is a player (melee damage)
            attacker = (Player) e.getDamager();
        } else if(e.getDamager() instanceof Arrow) {
            // Attacker is an arrow (projectile damage)
            Arrow arrow = (Arrow) e.getDamager();
            if(!(arrow.getShooter() instanceof Player)) {
                // Arrow was not fired by a player
                return;
            }
            // Cast attacker
            attacker = (Player) arrow.getShooter();
        } else if(e.getDamager() instanceof ThrownPotion) {
        /* Splash potion of harming triggers this event because it deals direct damage,
        but we will deal with that kind of stuff in PotionSplashEvent instead */
            return;
        } else if(e.getDamager() instanceof Trident){
            Trident trident = (Trident) e.getDamager();
            if (!(trident.getShooter() instanceof Player)){
                return;
            }
            attacker = (Player) trident.getShooter();
        }


        // It's possible to shoot yourself
        if(victim == attacker) {
            return;
        }
        // Just a quick null check for the attacker, in case I missed something
        if(attacker == null) {
            return;
        }

        // Check the teams
        if(canPlayerHurt(victim)) {
            e.setCancelled(true);
        }
        if (canPlayerHurt(attacker)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent e) {
        // Is this a dangerous potion? (Probably not the most efficient way to check this. Tips?)
        boolean cancel = true;
        for(PotionEffect effect : e.getEntity().getEffects()) {
            if(effect.getType().getName().equalsIgnoreCase("harm") || // Splash potion of harming
                    effect.getType().getName().equalsIgnoreCase("poison")) { // Splash potion of poison
                cancel = false;
            }
        }


        if(cancel) return;

        // Figure out who threw it
        if(!(e.getPotion().getShooter() instanceof Player)) {
            // The potion was not thrown by a player. Probably just some crazy witch again.
            return;
        }

        // Cast attacker
        Player attacker = (Player) e.getPotion().getShooter();

        // Check each entity that was hit by this potion
        Player victim;
        for(LivingEntity entity : e.getAffectedEntities()) {
            if(entity instanceof Player) {
                // This victim is a player, cast him/her
                victim = (Player) entity;

                // You can easily hit yourself with a splash potion.
                if(victim == attacker) {
                    // Yeah, this is the same player. Let him burn! Next!
                    continue;
                }

                // Check teams
                if(canPlayerHurt(victim)) {
                    // Reduce the effect of this potion to zero (victim only)
                    e.setIntensity(victim, 0);
                }

                if (canPlayerHurt(attacker)){
                    e.setIntensity(victim, 0);
                }
            }
        }
    }



    @Override
    public void onDisable() {
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(pvpPath));
            PVPData data = new PVPData(noPVP);
            System.out.println(gson.toJson(data));
            writer.write(gson.toJson(data));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
