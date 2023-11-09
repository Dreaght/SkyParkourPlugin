package net.dreaght.parkour;

import net.dreaght.parkour.commands.MenuCommand;
import net.dreaght.parkour.listeners.MenuListener;
import net.dreaght.parkour.listeners.OnPressListener;
import net.dreaght.parkour.listeners.PlacingListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Parkour extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("pc").setExecutor(new MenuCommand());
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new PlacingListener(), this);
        getServer().getPluginManager().registerEvents(new OnPressListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
