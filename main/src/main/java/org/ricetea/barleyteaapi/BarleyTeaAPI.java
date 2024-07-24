package org.ricetea.barleyteaapi;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.block.registration.BlockRegister;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.api.entity.counter.TickCounter;
import org.ricetea.barleyteaapi.api.entity.counter.TickCounterTrigger;
import org.ricetea.barleyteaapi.api.entity.counter.TickingService;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.api.event.BarleyTeaAPILoadEvent;
import org.ricetea.barleyteaapi.api.event.BarleyTeaAPIUnloadEvent;
import org.ricetea.barleyteaapi.api.internal.chunk.ChunkStorage;
import org.ricetea.barleyteaapi.api.internal.entity.EntityHelperInternals;
import org.ricetea.barleyteaapi.api.internal.entity.counter.TickCounterConstuctors;
import org.ricetea.barleyteaapi.api.internal.entity.counter.TickingServices;
import org.ricetea.barleyteaapi.api.internal.misc.MiscInternalFunctions;
import org.ricetea.barleyteaapi.api.internal.nms.INMSEntryPoint;
import org.ricetea.barleyteaapi.api.internal.nms.NMSVersion;
import org.ricetea.barleyteaapi.api.item.registration.*;
import org.ricetea.barleyteaapi.api.item.render.ItemRenderer;
import org.ricetea.barleyteaapi.api.localization.LocalizationRegister;
import org.ricetea.barleyteaapi.api.misc.RandomProvider;
import org.ricetea.barleyteaapi.api.task.TaskService;
import org.ricetea.barleyteaapi.internal.block.registration.BlockRegisterImpl;
import org.ricetea.barleyteaapi.internal.chunk.ChunkStorageImpl;
import org.ricetea.barleyteaapi.internal.connector.BulitInSoftDepend;
import org.ricetea.barleyteaapi.internal.connector.ExcellentEnchantsConnector;
import org.ricetea.barleyteaapi.internal.connector.GeyserConnector;
import org.ricetea.barleyteaapi.internal.connector.ProtocolLibConnector;
import org.ricetea.barleyteaapi.internal.entity.EntityHelperInternalsImpl;
import org.ricetea.barleyteaapi.internal.entity.counter.AsyncTickingServiceImpl;
import org.ricetea.barleyteaapi.internal.entity.counter.PersistentTickCounterImpl;
import org.ricetea.barleyteaapi.internal.entity.counter.SyncTickingServiceImpl;
import org.ricetea.barleyteaapi.internal.entity.counter.TransientTickCounterImpl;
import org.ricetea.barleyteaapi.internal.entity.registration.EntityRegisterImpl;
import org.ricetea.barleyteaapi.internal.item.registration.*;
import org.ricetea.barleyteaapi.internal.item.renderer.DefaultItemRendererImpl;
import org.ricetea.barleyteaapi.internal.item.renderer.DefaultItemRendererImpl2;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.barleyteaapi.internal.listener.*;
import org.ricetea.barleyteaapi.internal.listener.monitor.*;
import org.ricetea.barleyteaapi.internal.localization.LocalizationRegisterImpl;
import org.ricetea.barleyteaapi.internal.misc.ThreadLocalRandomProviderImpl;
import org.ricetea.barleyteaapi.internal.task.BlockTickTask;
import org.ricetea.barleyteaapi.internal.task.EntityTickTask;
import org.ricetea.barleyteaapi.internal.task.ItemTickTask;
import org.ricetea.barleyteaapi.internal.task.TaskServiceImpl;
import org.ricetea.barleyteaapi.util.connector.SoftDependRegister;
import org.ricetea.utils.ObjectUtil;
import org.ricetea.utils.SupplierUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
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
    public INMSEntryPoint getNMSEntryPoint() {
        return nmsEntryPoint;
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
        Logger logger = getLogger();
        logger.info("initializing API...");
        loadApiImplementations();
        logger.info("initializing NMS Feature...");
        loadNMSFeature();
        logger.info("initializing Soft-depends");
        SoftDependRegister<BarleyTeaAPI> softDependRegister = new SoftDependRegister<>(this);
        softDependRegister.register(BulitInSoftDepend.ExcellentEnchants,
                SupplierUtil.fromConstuctor(ExcellentEnchantsConnector.class));
        softDependRegister.register(BulitInSoftDepend.ProtocolLib,
                SupplierUtil.fromConstuctor(ProtocolLibConnector.class));
        softDependRegister.register(BulitInSoftDepend.Geyser,
                SupplierUtil.fromConstuctor(GeyserConnector.class));
        this.softDependRegister = softDependRegister;
        softDependRegister.reloadAll();
        logger.info("registering event listeners");
        registerEventListeners();
        registerEventMonitors();
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

    private void registerEventMonitors() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(EntityDamageMonitor.getInstance(), this);
        pluginManager.registerEvents(EntityDeathMonitor.getInstance(), this);
        pluginManager.registerEvents(EntityMountMonitor.getInstance(), this);
        pluginManager.registerEvents(EntityMoveMonitor.getInstance(), this);
        pluginManager.registerEvents(EntityTameMonitor.getInstance(), this);
        pluginManager.registerEvents(EntityDamageMonitor.getInstance(), this);
        pluginManager.registerEvents(EntityTransformMonitor.getInstance(), this);
        pluginManager.registerEvents(ProjectileMonitor.getInstance(), this);
        pluginManager.registerEvents(SlimeSplitMonitor.getInstance(), this);
    }

    private void loadApiImplementations() {
        final ServicesManager servicesManager = Bukkit.getServicesManager();
        loadApiImplementation(servicesManager, new BlockRegisterImpl(), BlockRegister.class);
        loadApiImplementation(servicesManager, new TickCounterConstuctors() {
            @Nonnull
            @Override
            public TickCounter persistentCounter(@Nonnull NamespacedKey key,
                                                 @Nonnull IntUnaryOperator operator,
                                                 @Nonnull TickCounterTrigger trigger,
                                                 int startValue) {
                return new PersistentTickCounterImpl(key, operator, trigger, startValue);
            }

            @Nonnull
            @Override
            public TickCounter transistentCounter(@Nonnull NamespacedKey key,
                                                  @Nonnull IntUnaryOperator operator,
                                                  @Nonnull TickCounterTrigger trigger,
                                                  int startValue) {
                return new TransientTickCounterImpl(key, operator, trigger, startValue);
            }
        }, TickCounterConstuctors.class);
        loadApiImplementation(servicesManager, new TickingServices() {
            @Nonnull
            private final TickingService syncService = new SyncTickingServiceImpl(),
                    asyncService = new AsyncTickingServiceImpl();

            @Nonnull
            @Override
            public TickingService syncService() {
                return syncService;
            }

            @Nonnull
            @Override
            public TickingService asyncService() {
                return asyncService;
            }
        }, TickingServices.class);
        loadApiImplementation(servicesManager, new MiscInternalFunctions() {
            @Override
            public <T extends Entity> boolean tryRegisterEntityAfterSpawn
                    (@Nonnull CustomEntity entityType, @Nonnull T entity, @Nullable Predicate<T> predicate) {
                if (EntityHelper.tryRegister(entityType, entity, predicate)) {
                    EntityFeatureLinker.loadEntity(entityType, entity, false);
                    return true;
                } else {
                    entity.remove();
                    return false;
                }
            }
        }, MiscInternalFunctions.class);
        loadApiImplementation(servicesManager, new EntityRegisterImpl(), EntityRegister.class);
        loadApiImplementation(servicesManager, new ChunkStorageImpl(), ChunkStorage.class);
        loadApiImplementation(servicesManager, new CookingRecipeRegisterImpl(), CookingRecipeRegister.class);
        loadApiImplementation(servicesManager, new CraftingRecipeRegisterImpl(), CraftingRecipeRegister.class);
        loadApiImplementation(servicesManager, new ItemRegisterImpl(), ItemRegister.class);
        loadApiImplementation(servicesManager, new ItemRendererRegisterImpl(), ItemRendererRegister.class);
        loadApiImplementation(servicesManager, new ItemSubRendererRegisterImpl(), ItemSubRendererRegister.class);
        loadApiImplementation(servicesManager, new SmithingRecipeRegisterImpl(), SmithingRecipeRegister.class);
        loadApiImplementation(servicesManager, new LocalizationRegisterImpl(), LocalizationRegister.class);
        loadApiImplementation(servicesManager, new ThreadLocalRandomProviderImpl(), RandomProvider.class);
        loadApiImplementation(servicesManager, new TaskServiceImpl(), TaskService.class);
        loadApiImplementation(servicesManager, new EntityHelperInternalsImpl(), EntityHelperInternals.class);
        if (NMSVersion.getCurrent().getVersion() < NMSVersion.v1_20_R4.getVersion())
            loadApiImplementation(servicesManager, new DefaultItemRendererImpl(), ItemRenderer.class);
        else
            loadApiImplementation(servicesManager, new DefaultItemRendererImpl2(), ItemRenderer.class);
    }

    public <T> void loadApiImplementation(@Nonnull ServicesManager servicesManager,
                                           @Nonnull T service, @Nonnull Class<T> serviceClazz) {
        servicesManager.register(serviceClazz, service, this, ServicePriority.Lowest);
    }

    private void loadNMSFeature() {
        NMSVersion nmsVersion = NMSVersion.getCurrent();
        String nmsVersionInString = nmsVersion.toString();
        String minecraftVersion = Bukkit.getMinecraftVersion();
        Logger logger = getLogger();
        if (!nmsVersion.isValid()) {
            logger.info("Failed to loading NMS Support for \"" + minecraftVersion + "\", some features will be lost!");
            return;
        }
        String className = "org.ricetea.barleyteaapi.internal.nms." + nmsVersionInString + ".NMSEntryPoint";
        Supplier<?> nmsSupplier = ObjectUtil.tryMap(() -> {
            try {
                return SupplierUtil.fromConstuctor(Class.forName(className), this);
            } catch (ClassNotFoundException e) {
                logger.log(Level.WARNING, "Cannot loaded NMS Support for " + minecraftVersion + "\", some feature will be lost!", e);
                return null;
            }
        });
        if (nmsSupplier == null) {
            return;
        }
        if (nmsSupplier.get() instanceof INMSEntryPoint entryPoint) {
            logger.info("Found NMS Support Class '" + className + "', Loading...");
            try {
                entryPoint.onEnable();
            } catch (Exception e) {
                logger.log(Level.WARNING, "Cannot loaded NMS Support for \"" + minecraftVersion + "\", some feature will be lost!", e);
                return;
            }
            this.nmsEntryPoint = entryPoint;
            logger.info("Successfully loaded NMS Support for \"" + minecraftVersion + "\" !");
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
        ObjectUtil.safeCall(TickingService.syncServiceUnsafe(), TickingService::shutdown);
        ObjectUtil.safeCall(TickingService.asyncServiceUnsafe(), TickingService::shutdown);
        ObjectUtil.safeCall(TaskService.getInstanceUnsafe(), TaskService::shutdown);
        Bukkit.getServicesManager().unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        _inst = null;
        logger.info("BarleyTeaAPI successfully unloaded!");
    }
}
