package org.ricetea.barleyteaapi;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.entity.counter.TickingService;
import org.ricetea.barleyteaapi.api.event.BarleyTeaAPILoadEvent;
import org.ricetea.barleyteaapi.api.event.BarleyTeaAPIUnloadEvent;
import org.ricetea.barleyteaapi.api.item.registration.CraftingRecipeRegister;
import org.ricetea.barleyteaapi.api.task.TaskService;
import org.ricetea.barleyteaapi.internal.connector.ExcellentEnchantsConnector;
import org.ricetea.barleyteaapi.internal.connector.ProtocolLibConnector;
import org.ricetea.barleyteaapi.internal.item.renderer.DefaultItemRendererImpl;
import org.ricetea.barleyteaapi.internal.listener.*;
import org.ricetea.barleyteaapi.internal.task.BlockTickTask;
import org.ricetea.barleyteaapi.internal.task.EntityTickTask;
import org.ricetea.barleyteaapi.internal.task.ItemTickTask;
import org.ricetea.barleyteaapi.util.connector.SoftDependRegister;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.logging.Logger;

@Singleton
@ApiStatus.Internal
public final class BarleyTeaAPI extends JavaPlugin {
    private static BarleyTeaAPI _inst;
    @Nullable
    private SoftDependRegister<BarleyTeaAPI> softDependRegister;

    @Nonnull
    public static BarleyTeaAPI getInstance() {
        return Objects.requireNonNull(_inst);
    }

    @Nullable
    public static BarleyTeaAPI getInstanceUnsafe() {
        return _inst;
    }

    @Nullable
    public static BarleyTeaAPI getInstanceUnsafeAndCheck() {
        BarleyTeaAPI inst = _inst;
        if (inst == null) {
            Bukkit.getLogger().warning("BarleyTeaAPI isn't loaded, all of the features won't worked!");
        } else if (!inst.isEnabled()) {
            Bukkit.getLogger().warning("BarleyTeaAPI isn't enabled, all of the features won't worked!");
        }
        return inst;
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

    @Nonnull
    public SoftDependRegister<BarleyTeaAPI> getSoftDependRegister() {
        return Objects.requireNonNull(softDependRegister);
    }

    @Override
    public void onEnable() {
        _inst = this;
        SoftDependRegister<BarleyTeaAPI> softDependRegister = new SoftDependRegister<>(this);
        softDependRegister.register(ObjectUtil.getSupplierOfConstructor(ExcellentEnchantsConnector.class));
        softDependRegister.register(ObjectUtil.getSupplierOfConstructor(ProtocolLibConnector.class));
        this.softDependRegister = softDependRegister;
        softDependRegister.reloadAll();
        Logger logger = getLogger();
        logger.info("registering listeners");
        registerEventListeners();
        logger.info("initializing API...");
        DefaultItemRendererImpl.getInstance().checkIsRegistered();
        logger.info("BarleyTeaAPI successfully loaded!");
        Bukkit.getPluginManager().callEvent(new BarleyTeaAPILoadEvent());
    }

    private void registerEventListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(BlockListener.getInstance(), this);
        pluginManager.registerEvents(ChunkListener.getInstance(), this);
        pluginManager.registerEvents(CookListener.getInstance(), this);
        pluginManager.registerEvents(CraftListener.getInstance(), this);
        pluginManager.registerEvents(EntityChangeEnvironmentListener.getInstance(), this);
        pluginManager.registerEvents(EntityDamageListener.getInstance(), this);
        pluginManager.registerEvents(EntityDeathListener.getInstance(), this);
        pluginManager.registerEvents(EntityMountListener.getInstance(), this);
        pluginManager.registerEvents(EntityMoveListener.getInstance(), this);
        pluginManager.registerEvents(EntitySpawnListener.getInstance(), this);
        pluginManager.registerEvents(EntityTameListener.getInstance(), this);
        pluginManager.registerEvents(EntityTargetListener.getInstance(), this);
        pluginManager.registerEvents(EntityTransformListener.getInstance(), this);
        pluginManager.registerEvents(InventoryEventListener.getInstance(), this);
        pluginManager.registerEvents(PlayerEventListener.getInstance(), this);
        pluginManager.registerEvents(ProjectileListener.getInstance(), this);
        pluginManager.registerEvents(SlimeSplitListener.getInstance(), this);
        pluginManager.registerEvents(SmithingListener.getInstance(), this);
    }

    @Override
    public void onDisable() {
        Logger logger = getLogger();
        logger.info("uninitializing API...");
        Bukkit.getPluginManager().callEvent(new BarleyTeaAPIUnloadEvent());
        ObjectUtil.safeCall(softDependRegister, SoftDependRegister::unregisterAll);
        ObjectUtil.safeCall(CraftingRecipeRegister.getInstanceUnsafe(), CraftingRecipeRegister::unregisterAll);
        ObjectUtil.safeCall(EntityTickTask.getInstanceUnsafe(), EntityTickTask::stop);
        ObjectUtil.safeCall(ItemTickTask.getInstanceUnsafe(), ItemTickTask::stop);
        ObjectUtil.safeCall(BlockTickTask.getInstanceUnsafe(), BlockTickTask::stop);
        ObjectUtil.safeCall(TickingService.syncService(), TickingService::shutdown);
        ObjectUtil.safeCall(TickingService.asyncService(), TickingService::shutdown);
        ObjectUtil.safeCall(TaskService.getInstance(), TaskService::shutdown);
        Bukkit.getScheduler().cancelTasks(this);
        _inst = null;
        logger.info("BarleyTeaAPI successfully unloaded!");
    }
}
