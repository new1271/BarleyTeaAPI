package org.ricetea.barleyteaapi;

import java.util.Iterator;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.counter.TickingService;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureBarleyTeaAPILoad;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.api.event.BarleyTeaAPILoadEvent;
import org.ricetea.barleyteaapi.api.event.BarleyTeaAPIUnloadEvent;
import org.ricetea.barleyteaapi.api.task.TaskService;
import org.ricetea.barleyteaapi.internal.bridge.ExcellentEnchantsBridge;
import org.ricetea.barleyteaapi.internal.listener.*;
import org.ricetea.barleyteaapi.internal.nms.BarleySummonCommand;
import org.ricetea.barleyteaapi.test.TestEntity;

public final class BarleyTeaAPI extends JavaPlugin {
    private static BarleyTeaAPI _inst;
    public boolean hasExcellentEnchants;

    @Nullable
    public static BarleyTeaAPI getInstance() {
        return _inst;
    }

    public static void registerTestEntity() {
        EntityRegister.getInstance().register(TestEntity.getInstance());
    }

    @Override
    public void onEnable() {
        _inst = this;
        Logger logger = getLogger();
        logger.info("checking soft depends");
        if (checkSoftDepend("ExcellentEnchants")) {
            hasExcellentEnchants = true;
            ExcellentEnchantsBridge.registerTranslations();
        }
        logger.info("registering listeners");
        registerEventListeners();
        logger.info("registering '/summonbarley' command...");
        BarleySummonCommand.register();
        logger.info("initializing API...");
        announceEntitiesAPILoaded();
        logger.info("BarleyTeaAPI successfully loaded!");
        Bukkit.getPluginManager().callEvent(new BarleyTeaAPILoadEvent());
    }

    private void registerEventListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(ChunkListener.getInstance(), this);
        pluginManager.registerEvents(CreatureSpawnListener.getInstance(), this);
        pluginManager.registerEvents(EntityDamageListener.getInstance(), this);
        pluginManager.registerEvents(EntityDeathListener.getInstance(), this);
        pluginManager.registerEvents(EntityMountListener.getInstance(), this);
        pluginManager.registerEvents(EntityTargetListener.getInstance(), this);
        pluginManager.registerEvents(EntityTransformListener.getInstance(), this);
        pluginManager.registerEvents(ProjectileListener.getInstance(), this);
        pluginManager.registerEvents(SlimeSplitListener.getInstance(), this);
    }

    private boolean checkSoftDepend(String pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        Logger logger = getLogger();
        if (plugin == null) {
            logger.info(pluginName + ": not found.");
            return false;
        }
        if (plugin.isEnabled()) {
            logger.info(pluginName + ": found, and it is enabled.");
            return true;
        } else {
            logger.warning(pluginName + ": found, but it is disabled.");
            return false;
        }
    }

    private void announceEntitiesAPILoaded() {
        EntityRegister register = EntityRegister.getInstance();
        if (register.hasAnyRegisteredMob()) {
            for (Iterator<World> worldIterator = Bukkit.getWorlds().iterator(); worldIterator.hasNext();) {
                World world = worldIterator.next();
                if (world != null) {
                    for (Iterator<Entity> entityIterator = world.getEntities().iterator(); entityIterator.hasNext();) {
                        Entity entity = entityIterator.next();
                        NamespacedKey id = BaseEntity.getEntityID(entity);
                        if (id != null) {
                            BaseEntity entityType = register.lookupEntityType(id);
                            if (entityType != null && entityType instanceof FeatureBarleyTeaAPILoad) {
                                FeatureBarleyTeaAPILoad apiLoadEntity = (FeatureBarleyTeaAPILoad) entityType;
                                apiLoadEntity.handleAPILoaded(entity);
                            }
                        }
                    }
                }
            }
        }
    }

    private void announceEntitiesAPIUnloaded() {
        EntityRegister register = EntityRegister.getInstance();
        if (register.hasAnyRegisteredMob()) {
            for (Iterator<World> worldIterator = Bukkit.getWorlds().iterator(); worldIterator.hasNext();) {
                World world = worldIterator.next();
                if (world != null) {
                    for (Iterator<Entity> entityIterator = world.getEntities().iterator(); entityIterator.hasNext();) {
                        Entity entity = entityIterator.next();
                        NamespacedKey id = BaseEntity.getEntityID(entity);
                        if (id != null) {
                            BaseEntity entityType = register.lookupEntityType(id);
                            if (entityType != null && entityType instanceof FeatureBarleyTeaAPILoad) {
                                FeatureBarleyTeaAPILoad apiLoadEntity = (FeatureBarleyTeaAPILoad) entityType;
                                apiLoadEntity.handleAPIUnloaded(entity);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getPluginManager().callEvent(new BarleyTeaAPIUnloadEvent());
        if (hasExcellentEnchants) {
            ExcellentEnchantsBridge.unregisterTranslations();
        }
        announceEntitiesAPIUnloaded();
        TickingService.shutdown();
        TaskService.shutdown();
        Bukkit.getScheduler().cancelTasks(this);
        _inst = null;
    }

    public static boolean checkPluginUsable() {
        BarleyTeaAPI inst = _inst;
        if (inst == null) {
            Bukkit.getLogger().warning("BarleyTeaAPI isn't loaded, all of the features won't worked!");
            return false;
        } else if (!inst.isEnabled()) {
            Bukkit.getLogger().warning("BarleyTeaAPI isn't enabled, all of the features won't worked!");
            return false;
        }
        return true;
    }

    public static void warnWhenPluginUsable(String warnString) {
        BarleyTeaAPI inst = _inst;
        if (inst == null || !inst.isEnabled()) {
            Bukkit.getLogger().warning(warnString);
        }
    }
}
