package org.ricetea.barleyteaapi.internal.nms.v1_20_R2;

import com.google.common.collect.Multimap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.command.CommandRegister;
import org.ricetea.barleyteaapi.api.event.EntitiesRegisteredEvent;
import org.ricetea.barleyteaapi.api.event.EntitiesUnregisteredEvent;
import org.ricetea.barleyteaapi.api.event.ItemsRegisteredEvent;
import org.ricetea.barleyteaapi.api.event.ItemsUnregisteredEvent;
import org.ricetea.barleyteaapi.internal.nms.*;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R2.command.NMSCommandRegisterImpl;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R2.command.NMSGiveCommand;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R2.command.NMSRegularCommand;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R2.command.NMSSummonCommand;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R2.helper.NBTItemHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R2.helper.NBTTagCompoundHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R2.helper.NMSEntityHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R2.helper.NMSItemHelper;

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

    public void onEnable() {
        Logger logger = apiInst.getLogger();
        logger.info("registering command register...");
        NMSCommandRegisterImpl commandRegister = new NMSCommandRegisterImpl();
        CommandRegister.setInstance(commandRegister, NMSCommandRegisterImpl.class);
        logger.info("registering '/givebarley' command...");
        commandRegister.register(giveCommand = new NMSGiveCommand());
        logger.info("registering '/summonbarley' command...");
        commandRegister.register(summonCommand = new NMSSummonCommand());
        logger.info("registering nms function...");
        NMSHelperRegister.setHelper(NMSEntityHelper::damage, INMSEntityHelper.class);
        NMSHelperRegister.setHelper(new INMSItemHelper() {
            @Nullable
            @Override
            public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@Nullable Material material) {
                return NMSItemHelper.getDefaultAttributeModifiers(material);
            }

            @Nullable
            @Override
            public ItemStack createItemStackFromNbtString(@Nonnull String nbt) {
                return NMSItemHelper.createItemStackFromNbtString(nbt);
            }
        }, INMSItemHelper.class);
        NMSHelperRegister.setHelper(new INBTItemHelper() {
            @Nonnull
            @Override
            public ItemStack copyNbtWhenSmithing(@Nonnull ItemStack original, @Nonnull ItemStack itemStackCopying) {
                var compound = NBTItemHelper.getNBT(original);
                var compound2 = NBTItemHelper.getNBT(itemStackCopying);
                return NBTItemHelper.setNBT(original, NBTTagCompoundHelper.merge(compound2, compound));
            }
        }, INBTItemHelper.class);
        Bukkit.getPluginManager().registerEvents(this, apiInst);
    }

    public void onDisable() {
        Logger logger = apiInst.getLogger();
        logger.info("unregistering command register...");
        NMSCommandRegisterImpl commandRegister = CommandRegister.getInstanceUnsafe(NMSCommandRegisterImpl.class);
        if (commandRegister != null) {
            commandRegister.unregisterAll();
            CommandRegister.setInstance(null, NMSCommandRegisterImpl.class);
        }
        logger.info("unregistering nms function...");
        NMSHelperRegister.setHelper(null, INMSEntityHelper.class);
        NMSHelperRegister.setHelper(null, INMSItemHelper.class);
    }

    @EventHandler
    public void listenEntitiesRegistered(EntitiesRegisteredEvent event) {
        if (event == null)
            return;
        summonCommand.updateSuggestions();
    }

    @EventHandler
    public void listenEntitiesUnregistered(EntitiesUnregisteredEvent event) {
        if (event == null)
            return;
        summonCommand.updateSuggestions();
    }

    @EventHandler
    public void listenItemsRegistered(ItemsRegisteredEvent event) {
        if (event == null)
            return;
        giveCommand.updateSuggestions();
    }

    @EventHandler
    public void listenItemsUnregistered(ItemsUnregisteredEvent event) {
        if (event == null)
            return;
        giveCommand.updateSuggestions();
    }
}
