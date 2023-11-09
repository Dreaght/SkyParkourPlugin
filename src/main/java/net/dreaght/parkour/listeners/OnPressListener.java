package net.dreaght.parkour.listeners;

import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OnPressListener implements Listener {
    private final Plugin plugin;

    private final Map<Player, Integer> playerCheckpointMap;
    private final Map<Player, Location> playerNextCheckpointMap;
    private final Map<Player, Location> playerPreviousCheckpoint;

    public OnPressListener(Plugin plugin) {
        this.plugin = plugin;

        this.playerCheckpointMap = new HashMap<>();
        this.playerNextCheckpointMap = new HashMap<>();
        this.playerPreviousCheckpoint = new HashMap<>();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo().clone();
        Block blockIn = to.getBlock();
        Block blockOn = to.add(0, -1, 0).getBlock();
        World world = event.getPlayer().getWorld();

        if (blockIn.getType() == Material.GOLD_PLATE) {
            restartParkour(player, blockIn, world);
        }

        if (playerNextCheckpointMap.get(player) == null) {
            return;
        }

        if (blockOn.getLocation().equals(playerNextCheckpointMap.get(player))) {
            parkourCheckpoint(player, world);
        }

        if (playerCheckpointMap.get(player) == 0) {
            return;
        }

        if (blockIn.getY() < (playerNextCheckpointMap.get(player).getY())) {
            player.sendMessage(ChatColor.RED + "You lose!");

            if (playerPreviousCheckpoint.get(player) != null) {
                world.getBlockAt(playerPreviousCheckpoint.get(player)).setType(Material.AIR);
            }

            world.getBlockAt(playerNextCheckpointMap.get(player)).setType(Material.AIR);

            playerNextCheckpointMap.remove(player);
            playerPreviousCheckpoint.remove(player);
        }

    }

    public void restartParkour(Player player, Block startBlock, World world) {
        playerCheckpointMap.put(player, 0);

        Location nextBlockPos = (startBlock.getLocation().add(
                getRandom(new int[]{-3, 3}),
                getRandom(new int[]{4, 5}),
                getRandom(new int[]{-3, 3})
        ));

        world.getBlockAt(nextBlockPos).setType(Material.IRON_BLOCK);

        playerNextCheckpointMap.put(player, nextBlockPos);
        player.teleport(nextBlockPos.clone().add(0.5, 1, 0.5));
    }

    public void parkourCheckpoint(Player player, World world) {
        Location currentCheckpointLoc = playerNextCheckpointMap.get(player);

        playerCheckpointMap.put(player, playerCheckpointMap.get(player) + 1);

        int nextDir = getRandom(new int[]{1, 2, 3, 4});

        Integer blockX = 0;
        Integer blockY = 0;
        Integer blockZ = 0;

        switch (nextDir) {
            case 1:
                blockX = getRandom(new int[]{-3, -2, -1, 0, 1, 2, 3});
                blockY = getRandom(new int[]{-1, 1, 1});
                blockZ = getRandom(new int[]{-3, 3});
                break;
            case 2:
                blockZ = getRandom(new int[]{-3, -2, -1, 0, 1, 2, 3});
                blockY = getRandom(new int[]{-1, 1, 1});
                blockX = getRandom(new int[]{-3, 3});
                break;
            case 3:
                blockX = getRandom(new int[]{-1, 0, 1});
                blockY = getRandom(new int[]{-1, 1, 1});
                blockZ = getRandom(new int[]{-4, 4});
                break;
            case 4:
                blockZ = getRandom(new int[]{-1, 0, 1});
                blockY = getRandom(new int[]{-1, 1, 1});
                blockX = getRandom(new int[]{-4, 4});
                break;
        }

        Location nextBlockPos = (currentCheckpointLoc.clone().add(blockX, blockY, blockZ));

        if (playerPreviousCheckpoint.get(player) != null) {
            world.getBlockAt(playerPreviousCheckpoint.get(player)).setType(Material.AIR);
        }

        world.getBlockAt(currentCheckpointLoc).setType(Material.GOLD_BLOCK);
        world.getBlockAt(nextBlockPos).setType(Material.IRON_BLOCK);

        spawnFireworks(currentCheckpointLoc, 1);


        player.sendMessage(ChatColor.GREEN + "You're got " + playerCheckpointMap.get(player) + "th checkpoint!");

        playerNextCheckpointMap.put(player, nextBlockPos);
        playerPreviousCheckpoint.put(player, currentCheckpointLoc);
    }

    public static void spawnFireworks(Location location, int amount){
        Location loc = location;
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();

        for(int i = 0;i<amount; i++){
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }


    public static int getRandom(int[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }
}
