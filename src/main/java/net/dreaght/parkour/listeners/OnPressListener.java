package net.dreaght.parkour.listeners;

import net.dreaght.parkour.ActionBar;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.PacketPlayOutBlockBreakAnimation;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class OnPressListener implements Listener {
    private final Plugin plugin;

    private final Map<Player, Location> startLocation;
    private final Map<Player, Integer> playerCheckpointMap;
    private final Map<Player, Location> playerNextCheckpointMap;
    private final Map<Player, Location> playerPreviousCheckpoint;

    public OnPressListener(Plugin plugin) {
        this.plugin = plugin;

        this.startLocation = new HashMap<>();
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

        if (blockIn.getY() < (playerNextCheckpointMap.get(player).getY()) - 1) {
            player.sendMessage(ChatColor.GREEN + String.format("You have finished on %d checkpoint!",
                    playerCheckpointMap.getOrDefault(player, 0)));

            final AtomicInteger count = new AtomicInteger(0);

            int addX = getRandom(new int[]{-2, 0, 2});
            int addZ = getRandom(new int[]{-2, 0, 2});

            Location addLoc = new Location(world, addX + 0.5, 2, addZ + 0.5);
            Location teleportTo = startLocation.getOrDefault(player, world.getSpawnLocation()).add(addLoc);

            new BukkitRunnable() {
                @Override
                public void run() {
                    count.incrementAndGet();
                    if (count.get() == 5) {
                        cancel();
                    }

                    player.teleport(teleportTo);
                }
            }.runTaskTimer(plugin, 1, 1);

            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 20, 0);

            if (playerPreviousCheckpoint.get(player) != null) {
                world.getBlockAt(playerPreviousCheckpoint.get(player)).setType(Material.AIR);
            }

            world.getBlockAt(playerNextCheckpointMap.get(player)).setType(Material.AIR);

            playerNextCheckpointMap.remove(player);
            playerPreviousCheckpoint.remove(player);
        }

    }

    public void restartParkour(Player player, Block startBlock, World world) {
        startLocation.put(player, startBlock.getLocation());
        playerCheckpointMap.put(player, 0);

        Location nextBlockPos = (startBlock.getLocation().add(
                getRandom(new int[]{-3, 3}),
                getRandom(new int[]{4, 5}),
                getRandom(new int[]{-3, 3})
        ));

        if (!world.getBlockAt(nextBlockPos).getType().equals(Material.AIR)) {
            return;
        }

        world.getBlockAt(nextBlockPos).setType(getRandomBlock());

        playerNextCheckpointMap.put(player, nextBlockPos);
        player.teleport(nextBlockPos.clone().add(0.5, 1, 0.5));

        startGame(player);
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
                blockZ = getRandom(new int[]{-2, 2});
                break;
            case 2:
                blockZ = getRandom(new int[]{-3, -2, -1, 0, 1, 2, 3});
                blockY = getRandom(new int[]{-1, 1, 1});
                blockX = getRandom(new int[]{-2, 2});
                break;
            case 3:
                blockX = getRandom(new int[]{-1, 0, 1});
                blockY = getRandom(new int[]{-1, 1, 1});
                blockZ = getRandom(new int[]{-3, 3});
                break;
            case 4:
                blockZ = getRandom(new int[]{-1, 0, 1});
                blockY = getRandom(new int[]{-1, 1, 1});
                blockX = getRandom(new int[]{-3, 3});
                break;
        }

        Location nextBlockPos = (currentCheckpointLoc.clone().add(blockX, blockY, blockZ));

        if (!world.getBlockAt(nextBlockPos).getType().equals(Material.AIR)) {
            return;
        }

        if (playerPreviousCheckpoint.get(player) != null) {
            world.getBlockAt(playerPreviousCheckpoint.get(player)).setType(Material.AIR);
        }

        world.getBlockAt(nextBlockPos).setType(getRandomBlock());

        player.playSound(currentCheckpointLoc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                20, getRandom(new int[]{0,1,2,3,4,5}));

        playerNextCheckpointMap.put(player, nextBlockPos);
        playerPreviousCheckpoint.put(player, currentCheckpointLoc);

        startBlockDestroying(currentCheckpointLoc.getBlock());
    }

    public void startGame(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                new ActionBar(plugin).sendActionBar(player,
                        ChatColor.GREEN +
                                String.format("You're at %d checkpoint!",
                                        playerCheckpointMap.getOrDefault(player, 0)));

                if (playerNextCheckpointMap.get(player) == null) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 5, 5);
    }

    public void startBlockDestroying(Block block) {
        new BukkitRunnable() {
            private int destroyStage = -1;
            private final BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());

            @Override
            public void run() {
                destroyStage++;
                if (destroyStage > 9) {
                    block.setType(Material.AIR);
                    this.cancel();
                    return;
                }

                PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(0, blockPosition, destroyStage);
                block.getWorld().getPlayers().forEach(player -> {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                });
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    public Material getRandomBlock() {
        List<Material> materials = Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .filter(Predicate.not(Material::hasGravity))
                .filter(Material::isOccluding)
                .filter(material -> material != Material.BARRIER)
                .collect(Collectors.toList());

        int randomIndex = new Random().nextInt(materials.size());
        return materials.get(randomIndex);
    }


    public static int getRandom(int[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }
}
