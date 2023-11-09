package net.dreaght.parkour.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;


public class PlacingListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        Location blockPos = block.getLocation();

        ItemStack placedItem = event.getItemInHand();

        if (placedItem == null || placedItem.getType() != Material.GOLD_PLATE || !(placedItem.getItemMeta().getDisplayName().
                equalsIgnoreCase(ChatColor.GREEN + "Parkour Start"))) {
            return;
        }

        World world = event.getBlockPlaced().getWorld();

        ArmorStand hologram = world.spawn(blockPos.add(0.5, -1, 0.5), ArmorStand.class);

        hologram.setCustomName(ChatColor.GREEN + "Start");
        hologram.setVisible(false);
        hologram.setCustomNameVisible(true);
        hologram.setGravity(false);
        hologram.setBasePlate(false);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.GOLD_PLATE) {
            for (Entity entity : block.getChunk().getEntities()) {
                if (entity.getLocation().equals(block.getLocation().add(0.5, -1, 0.5))) {
                    entity.remove();
                }
            }
        }
    }
}
