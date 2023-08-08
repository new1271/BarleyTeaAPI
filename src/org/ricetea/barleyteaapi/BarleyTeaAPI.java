package org.ricetea.barleyteaapi;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.ricetea.barleyteaapi.internal.listener.*;
import org.ricetea.barleyteaapi.internal.nms.BarleySummonCommand;

public final class BarleyTeaAPI extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("registering listeners");
        registerEventListeners();
        getLogger().info("registering '/summonbarley' command...");
        BarleySummonCommand.register();
        getLogger().info("BarleyTeaAPI successfully loaded!");
    }

    private void registerEventListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(EntityDamageListener.getInstance(), this);
        pluginManager.registerEvents(EntityDeathListener.getInstance(), this);
    }
}
