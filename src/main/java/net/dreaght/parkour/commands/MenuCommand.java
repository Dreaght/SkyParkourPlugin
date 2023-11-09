package net.dreaght.parkour.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class MenuCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = (Player) commandSender;
        player.openInventory(createInventory(player));

        return true;
    }

    public static Inventory createInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 9, ChatColor.AQUA + "Parkour Menu");

        ItemStack parkourStart = new ItemStack(Material.GOLD_PLATE, 1);
        ItemMeta parkourStartMeta = parkourStart.getItemMeta();

        parkourStartMeta.setDisplayName(ChatColor.GREEN + "Parkour Start");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Place this block to start parkour state!");
        parkourStartMeta.setLore(lore);

        parkourStart.setItemMeta(parkourStartMeta);

        inventory.setItem(4, parkourStart);

        return inventory;
    }
}
