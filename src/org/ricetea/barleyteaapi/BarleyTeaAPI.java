package org.ricetea.barleyteaapi;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.ricetea.barleyteaapi.internal.listener.*;
import org.ricetea.barleyteaapi.internal.nms.BarleySummonCommand;

public final class BarleyTeaAPI extends JavaPlugin {
    private static BarleyTeaAPI _inst;

    @Nullable
    public static BarleyTeaAPI getInstance() {
        return _inst;
    }

    @Override
    public void onEnable() {
        _inst = this;
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
        pluginManager.registerEvents(CreatureSpawnListener.getInstance(), this);
        pluginManager.registerEvents(ChunkListener.getInstance(), this);
        pluginManager.registerEvents(SlimeSplitListener.getInstance(), this);
    }

    @Override
    public void onDisable() {
        _inst = null;
    }

    public static void checkPluginUsable() {
        if (_inst == null) {
            Bukkit.getLogger().warning("BarleyTeaAPI isn't loaded, all of the entity features won't worked!");
        }
    }

    public static void warnWhenPluginUsable(String warnString) {
        if (_inst != null) {
            _inst.getLogger().warning(warnString);
        }
    }
}
