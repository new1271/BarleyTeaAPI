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
import org.ricetea.barleyteaapi.internal.connector.BulitInSoftDepend;
import org.ricetea.barleyteaapi.internal.connector.ExcellentEnchantsConnector;
import org.ricetea.barleyteaapi.internal.connector.GeyserConnector;
import org.ricetea.barleyteaapi.internal.connector.ProtocolLibConnector;
import org.ricetea.barleyteaapi.internal.item.renderer.DefaultItemRendererImpl;
import org.ricetea.barleyteaapi.internal.listener.*;
import org.ricetea.barleyteaapi.internal.nms.INMSEntryPoint;
import org.ricetea.barleyteaapi.internal.task.BlockTickTask;
import org.ricetea.barleyteaapi.internal.task.EntityTickTask;
import org.ricetea.barleyteaapi.internal.task.ItemTickTask;
import org.ricetea.barleyteaapi.util.NativeUtil;
import org.ricetea.barleyteaapi.util.connector.SoftDependRegister;
import org.ricetea.utils.ObjectUtil;
import org.ricetea.utils.SupplierUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Logger;

@Singleton
@ApiStatus.Internal
public final class BarleyTeaAPI extends JavaPlugin {
    private static BarleyTeaAPI _inst;
    @Nullable
    private SoftDependRegister<BarleyTeaAPI> softDependRegister;
    @Nullable
    private INMSEntryPoint nmsEntryPoint;

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
        softDependRegister.register(BulitInSoftDepend.ExcellentEnchants,
                SupplierUtil.fromConstuctor(ExcellentEnchantsConnector.class));
        softDependRegister.register(BulitInSoftDepend.ProtocolLib,
                SupplierUtil.fromConstuctor(ProtocolLibConnector.class));
        softDependRegister.register(BulitInSoftDepend.Geyser,
                SupplierUtil.fromConstuctor(GeyserConnector.class));
        this.softDependRegister = softDependRegister;
        softDependRegister.reloadAll();
        Logger logger = getLogger();
        logger.info("registering listeners");
        registerEventListeners();
        logger.info("initializing API...");
        DefaultItemRendererImpl.getInstance().checkIsRegistered();
        logger.info("initializing NMS Feature...");
        loadNMSFeature();
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

    private void loadNMSFeature() {
        String nmsVersion = NativeUtil.getNMSVersion();
        Logger logger = getLogger();
        logger.info("Loading NMS Support for \"" + nmsVersion + "\" ...");
        Exception ex = null;
        if (nmsVersion.startsWith("v_")) {
            nmsVersion = nmsVersion.replace("v_", "v");
        }
        final String finalNmsVersion = nmsVersion;
        Supplier<?> nmsSupplier = ObjectUtil.tryMap(() -> {
            try {
                return SupplierUtil.fromConstuctor(
                        Class.forName("org.ricetea.barleyteaapi.internal.nms." + finalNmsVersion + ".NMSEntryPoint"),
                        this);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        if (nmsSupplier == null) {
            ObjectUtil.safeCall(ex, Exception::printStackTrace);
            logger.warning("Cannot loaded NMS Support for \"" + nmsVersion + "\", some feature will be lost!");
            return;
        }
        if (nmsSupplier.get() instanceof INMSEntryPoint entryPoint) {
            try {
                entryPoint.onEnable();
            }
            catch (Exception e){
                e.printStackTrace();
                logger.warning("Cannot loaded NMS Support for \"" + nmsVersion + "\", some feature will be lost!");
                return;
            }
            this.nmsEntryPoint = entryPoint;
            logger.info("Successfully loaded NMS Support for \"" + nmsVersion + "\" !");
        }
    }

    @Override
    public void onDisable() {
        ObjectUtil.safeCall(nmsEntryPoint, INMSEntryPoint::onDisable);
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
