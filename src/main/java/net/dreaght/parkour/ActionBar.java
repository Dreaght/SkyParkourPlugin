package net.dreaght.parkour;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ActionBar {
    private final Plugin plugin;

    public static String v = null;

    public ActionBar(Plugin plugin) {
        this.plugin = plugin;

        v = plugin.getServer().getClass().getPackage().getName();
        v = v.substring(v.lastIndexOf(".") + 1);

    }

    public void sendActionBar(Player player, String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);

        try {
            if (ActionBar.v.equals("v1_12_R1") || ActionBar.v.startsWith("v1_13")) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            } else if (!(ActionBar.v.equalsIgnoreCase("v1_8_R1") || (ActionBar.v.contains("v1_7_")))) {
                Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + ActionBar.v + ".entity.CraftPlayer");
                Object p = c1.cast(player);
                Object ppoc;
                Class<?> c4 = Class.forName("net.minecraft.server." + ActionBar.v + ".PacketPlayOutChat");
                Class<?> c5 = Class.forName("net.minecraft.server." + ActionBar.v + ".Packet");

                Class<?> c2 = Class.forName("net.minecraft.server." + ActionBar.v + ".ChatComponentText");
                Class<?> c3 = Class.forName("net.minecraft.server." + ActionBar.v + ".IChatBaseComponent");
                Object o = c2.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(o, (byte) 2);

                Method getHandle = c1.getDeclaredMethod("getHandle");
                Object handle = getHandle.invoke(p);

                Field fieldConnection = handle.getClass().getDeclaredField("playerConnection");
                Object playerConnection = fieldConnection.get(handle);

                Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", c5);
                sendPacket.invoke(playerConnection, ppoc);
            } else {
                Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + ActionBar.v + ".entity.CraftPlayer");
                Object p = c1.cast(player);
                Object ppoc;
                Class<?> c4 = Class.forName("net.minecraft.server." + ActionBar.v + ".PacketPlayOutChat");
                Class<?> c5 = Class.forName("net.minecraft.server." + ActionBar.v+ ".Packet");

                Class<?> c2 = Class.forName("net.minecraft.server." + ActionBar.v + ".ChatSerializer");
                Class<?> c3 = Class.forName("net.minecraft.server." + ActionBar.v + ".IChatBaseComponent");
                Method m3 = c2.getDeclaredMethod("a", String.class);
                Object cbc = c3.cast(m3.invoke(c2, "{\"text\": \"" + message + "\"}"));
                ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(cbc, (byte) 2);

                Method getHandle = c1.getDeclaredMethod("getHandle");
                Object handle = getHandle.invoke(p);

                Field fieldConnection = handle.getClass().getDeclaredField("playerConnection");
                Object playerConnection = fieldConnection.get(handle);

                Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", c5);
                sendPacket.invoke(playerConnection, ppoc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Class<?> getNMSClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
    }
}
