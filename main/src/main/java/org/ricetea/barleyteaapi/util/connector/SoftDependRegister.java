package org.ricetea.barleyteaapi.util.connector;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.ricetea.utils.CollectionUtil;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SoftDependRegister<T extends Plugin> {

    @Nonnull
    private final Map<String, Supplier<? extends SoftDependConnector>> connectorMap = new ConcurrentHashMap<>();

    @Nonnull
    private final Map<String, SoftDependConnector> activeConnectorMap = new ConcurrentHashMap<>();

    @Nonnull
    private final T plugin;

    public SoftDependRegister(@Nonnull T plugin) {
        this.plugin = plugin;
    }

    @Nonnull
    public T getPlugin() {
        return plugin;
    }

    public void register(@Nullable Supplier<String> pluginNameSupplier, @Nullable Supplier<? extends SoftDependConnector> connectorSupplier) {
        if (pluginNameSupplier == null)
            return;
        register(pluginNameSupplier.get(), connectorSupplier);
    }

    public void register(@Nullable String pluginName, @Nullable Supplier<? extends SoftDependConnector> connectorSupplier) {
        if (pluginName == null || connectorSupplier == null)
            return;
        if (connectorMap.put(pluginName, connectorSupplier) != null) {
            ObjectUtil.safeCall(activeConnectorMap.remove(pluginName), connector ->
                    ObjectUtil.tryCall(connector::onDisable));
        }
    }

    public void unregister(@Nullable String pluginName) {
        if (pluginName == null)
            return;
        if (connectorMap.remove(pluginName) != null) {
            ObjectUtil.safeCall(activeConnectorMap.remove(pluginName), connector ->
                    ObjectUtil.tryCall(connector::onDisable));
        }
    }

    public void unregister(@Nullable Supplier<String> pluginNameSupplier) {
        if (pluginNameSupplier == null)
            return;
        unregister(pluginNameSupplier.get());
    }

    public void unregisterAll() {
        connectorMap.clear();
        CollectionUtil.forEachAndRemoveAll(activeConnectorMap.values(), connector ->
                ObjectUtil.safeCall(connector, SoftDependConnector::onDisable));
    }

    @Nullable
    public SoftDependConnector get(@Nullable String pluginName) {
        if (pluginName == null)
            return null;
        return activeConnectorMap.get(pluginName);
    }

    @Nullable
    public SoftDependConnector get(@Nullable Supplier<String> pluginNameSupplier) {
        if (pluginNameSupplier == null)
            return null;
        return get(pluginNameSupplier.get());
    }

    public void reloadAll() {
        CollectionUtil.forEachAndRemoveAll(activeConnectorMap.values(), connector ->
                ObjectUtil.safeCall(connector, SoftDependConnector::onDisable));
        Logger logger = plugin.getLogger();
        PluginManager pluginManager = Bukkit.getPluginManager();
        connectorMap.forEach((pluginName, connectorSupplier) -> {
            Plugin plugin = pluginManager.getPlugin(pluginName);
            if (plugin != null && plugin.isEnabled()) {
                SoftDependConnector connector = connectorSupplier.get();
                try {
                    connector.onEnable(plugin);
                } catch (Exception exception) {
                    logger.log(Level.WARNING,
                            "Cannot loaded " + connector.getClass().getName() + " for " + pluginName,
                            exception);
                    return;
                }
                activeConnectorMap.put(pluginName, connector);
                logger.info("Loaded " + connector.getClass().getName() + " for " + pluginName);
            }
        });
    }
}
