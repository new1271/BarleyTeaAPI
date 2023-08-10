package org.ricetea.barleyteaapi;

import java.util.Iterator;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.counter.TickingService;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureBarleyTeaAPILoad;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.api.event.BarleyTeaAPILoadEvent;
import org.ricetea.barleyteaapi.api.event.BarleyTeaAPIUnloadEvent;
import org.ricetea.barleyteaapi.api.task.TaskService;
import org.ricetea.barleyteaapi.internal.listener.*;
import org.ricetea.barleyteaapi.internal.nms.BarleySummonCommand;
import org.ricetea.barleyteaapi.test.TestEntity;

public final class BarleyTeaAPI extends JavaPlugin {
    private static BarleyTeaAPI _inst;

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
        getLogger().info("registering listeners");
        registerEventListeners();
        getLogger().info("registering '/summonbarley' command...");
        BarleySummonCommand.register();
        getLogger().info("initializing API...");
        announceEntitiesAPILoaded();
        getLogger().info("BarleyTeaAPI successfully loaded!");
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
        announceEntitiesAPIUnloaded();
        TickingService.shutdown();
        TaskService.shutdown();
        Bukkit.getScheduler().cancelTasks(this);
        _inst = null;
    }

    public static boolean checkPluginUsable() {
        if (_inst == null) {
            Bukkit.getLogger().warning("BarleyTeaAPI isn't loaded, all of the features won't worked!");
            return false;
        }
        return true;
    }

    public static void warnWhenPluginUsable(String warnString) {
        if (_inst != null) {
            _inst.getLogger().warning(warnString);
        }
    }
}
