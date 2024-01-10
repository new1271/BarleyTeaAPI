package org.ricetea.barleyteaapi.util.connector;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.utils.CollectionUtil;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SoftDependRegister<T extends Plugin> {

    @Nonnull
    private final Map<String, SoftDependConnector> connectorMap = new ConcurrentHashMap<>();

    @Nonnull
    private final Set<SoftDependConnector> activeConnectors = ConcurrentHashMap.newKeySet();

    @Nonnull
    private final T plugin;

    public SoftDependRegister(@Nonnull T plugin) {
        this.plugin = plugin;
    }

    @Nonnull
    public T getPlugin() {
        return plugin;
    }

    public void register(@Nullable Supplier<? extends SoftDependConnector> connectorSupplier) {
        if (connectorSupplier == null)
            return;
        register(connectorSupplier.get());
    }

    public void register(@Nullable SoftDependConnector connector) {
        if (connector == null)
            return;
        SoftDependConnector oldConnector = connectorMap.put(connector.getPluginName(), connector);
        if (oldConnector != null && activeConnectors.remove(oldConnector)) {
            oldConnector.onDisable();
        }
    }

    public void unregister(@Nullable SoftDependConnector connector) {
        if (connector == null)
            return;
        if (connectorMap.remove(connector.getPluginName(), connector) && activeConnectors.remove(connector)) {
            connector.onDisable();
        }
    }

    public void unregister(@Nullable String pluginName) {
        if (pluginName == null)
            return;
        SoftDependConnector oldConnector = connectorMap.remove(pluginName);
        if (oldConnector != null && activeConnectors.remove(oldConnector)) {
            oldConnector.onDisable();
        }
    }

    public void unregister(@Nullable Supplier<String> pluginNameSupplier) {
        if (pluginNameSupplier == null)
            return;
        SoftDependConnector oldConnector = connectorMap.remove(pluginNameSupplier.get());
        if (oldConnector != null && activeConnectors.remove(oldConnector)) {
            oldConnector.onDisable();
        }
    }

    public void unregisterAll() {
        connectorMap.clear();
        CollectionUtil.forEachAndRemoveAll(activeConnectors, connector ->
                ObjectUtil.safeCall(connector, SoftDependConnector::onDisable));
    }

    @Nullable
    public SoftDependConnector get(@Nullable String pluginName) {
        if (pluginName == null)
            return null;
        return connectorMap.get(pluginName);
    }

    @Nullable
    public SoftDependConnector get(@Nullable Supplier<String> pluginNameSupplier) {
        if (pluginNameSupplier == null)
            return null;
        return get(pluginNameSupplier.get());
    }

    @Nullable
    public SoftDependConnector getIfActived(@Nullable String pluginName) {
        if (pluginName == null)
            return null;
        SoftDependConnector result = get(pluginName);
        if (result != null) {
            return activeConnectors.contains(result) ? result : null;
        }
        return null;
    }

    @Nullable
    public SoftDependConnector getIfActived(@Nullable Supplier<String> pluginNameSupplier) {
        if (pluginNameSupplier == null)
            return null;
        return getIfActived(pluginNameSupplier.get());
    }

    public void reloadAll() {
        CollectionUtil.forEachAndRemoveAll(activeConnectors, connector ->
                ObjectUtil.safeCall(connector, SoftDependConnector::onDisable));
        BarleyTeaAPI api = BarleyTeaAPI.getInstanceUnsafeAndCheck();
        if (api == null)
            return;
        Logger logger = api.getLogger();
        PluginManager pluginManager = Bukkit.getPluginManager();
        connectorMap.forEach((pluginName, connector) -> {
            Plugin plugin = pluginManager.getPlugin(pluginName);
            if (plugin != null && plugin.isEnabled()) {
                try {
                    connector.onEnable(plugin);
                } catch (Exception exception) {
                    logger.log(Level.WARNING,
                            "Cannot loaded " + connector.getClass().getName() + " for " + pluginName,
                            exception);
                    return;
                }
                activeConnectors.add(connector);
                logger.info("Loaded " + connector.getClass().getName() + " for " + pluginName);
            }
        });
    }
}
