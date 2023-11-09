package net.dreaght.parkour.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class MenuListener implements Listener {
    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (!(event.getView().getTitle().equalsIgnoreCase(ChatColor.AQUA + "Parkour Menu"))) {
            return;
        }
        if (event.getCurrentItem() == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem().getType() == Material.GOLD_PLATE) {
            ItemStack parkourStart = getParkourStartItem();
            player.getInventory().addItem(parkourStart);
        }

        event.setCancelled(true);
    }

    public static ItemStack getParkourStartItem() {
        ItemStack parkourStart = new ItemStack(Material.GOLD_PLATE, 1);
        ItemMeta parkourStartMeta = parkourStart.getItemMeta();

        parkourStartMeta.setDisplayName(ChatColor.GREEN + "Parkour Start");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Place this block to start parkour state!");
        parkourStartMeta.setLore(lore);

        parkourStart.setItemMeta(parkourStartMeta);

        return parkourStart;
    }

}
