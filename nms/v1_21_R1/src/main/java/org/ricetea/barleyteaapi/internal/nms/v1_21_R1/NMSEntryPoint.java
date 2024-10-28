package org.ricetea.barleyteaapi.internal.nms.v1_21_R1;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicesManager;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.command.CommandRegister;
import org.ricetea.barleyteaapi.api.event.EntitiesRegisteredEvent;
import org.ricetea.barleyteaapi.api.event.EntitiesUnregisteredEvent;
import org.ricetea.barleyteaapi.api.event.ItemsRegisteredEvent;
import org.ricetea.barleyteaapi.api.event.ItemsUnregisteredEvent;
import org.ricetea.barleyteaapi.api.internal.nms.*;
import org.ricetea.barleyteaapi.internal.nms.v1_21_R1.command.NMSCommandRegisterImpl;
import org.ricetea.barleyteaapi.internal.nms.v1_21_R1.command.NMSGiveCommand;
import org.ricetea.barleyteaapi.internal.nms.v1_21_R1.command.NMSRegularCommand;
import org.ricetea.barleyteaapi.internal.nms.v1_21_R1.command.NMSSummonCommand;
import org.ricetea.barleyteaapi.internal.nms.v1_21_R1.helper.NMSEntityHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_21_R1.impl.NBTItemHelperImpl;
import org.ricetea.barleyteaapi.internal.nms.v1_21_R1.impl.NMSItemHelper2Impl;
import org.ricetea.barleyteaapi.internal.nms.v1_21_R1.impl.NMSItemHelperImpl;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.logging.Logger;

public final class NMSEntryPoint implements Listener, INMSEntryPoint {

    @Nullable
    private NMSRegularCommand summonCommand, giveCommand;

    @Nonnull
    private final BarleyTeaAPI apiInst;

    public NMSEntryPoint(@Nonnull BarleyTeaAPI apiInst) {
        this.apiInst = apiInst;
    }

    @Override
    public NMSVersion getNMSVersion() {
        return NMSVersion.v1_21_R1;
    }

    public void onEnable() {
        Logger logger = apiInst.getLogger();
        logger.info("[NMS] registering command register...");
        NMSCommandRegisterImpl commandRegister = new NMSCommandRegisterImpl();
        CommandRegister.setInstance(commandRegister, NMSCommandRegisterImpl.class);
        logger.info("[NMS] registering '/givebarley' command...");
        commandRegister.register(giveCommand = new NMSGiveCommand());
        logger.info("[NMS] registering '/summonbarley' command...");
        commandRegister.register(summonCommand = new NMSSummonCommand());
        logger.info("[NMS] registering nms implementation...");
        loadNmsImplementations();
        Bukkit.getPluginManager().registerEvents(this, apiInst);
    }

    private void loadNmsImplementations() {
        ServicesManager servicesManager = Bukkit.getServicesManager();
        apiInst.loadApiImplementation(servicesManager, NMSEntityHelper::damage, INMSEntityHelper.class);
        apiInst.loadApiImplementation(servicesManager, NMSItemHelperImpl.getInstance(), INMSItemHelper.class);
        apiInst.loadApiImplementation(servicesManager, NMSItemHelper2Impl.getInstance(), INMSItemHelper2.class);
        apiInst.loadApiImplementation(servicesManager, NBTItemHelperImpl.getInstance(), INBTItemHelper.class);
    }

    public void onDisable() {
        Logger logger = apiInst.getLogger();
        logger.info("unregistering command register...");
        NMSCommandRegisterImpl commandRegister = CommandRegister.getInstanceUnsafe(NMSCommandRegisterImpl.class);
        if (commandRegister != null) {
            commandRegister.unregisterAll();
            CommandRegister.setInstance(null, NMSCommandRegisterImpl.class);
        }
    }

    @EventHandler
    public void listenEntitiesRegistered(EntitiesRegisteredEvent event) {
        if (event == null)
            return;
        ObjectUtil.safeCall(summonCommand, NMSRegularCommand::updateSuggestions);
    }

    @EventHandler
    public void listenEntitiesUnregistered(EntitiesUnregisteredEvent event) {
        if (event == null)
            return;
        ObjectUtil.safeCall(summonCommand, NMSRegularCommand::updateSuggestions);
    }

    @EventHandler
    public void listenItemsRegistered(ItemsRegisteredEvent event) {
        if (event == null)
            return;
        ObjectUtil.safeCall(giveCommand, NMSRegularCommand::updateSuggestions);
    }

    @EventHandler
    public void listenItemsUnregistered(ItemsUnregisteredEvent event) {
        if (event == null)
            return;
        ObjectUtil.safeCall(giveCommand, NMSRegularCommand::updateSuggestions);
    }
}
