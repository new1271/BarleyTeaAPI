package org.ricetea.barleyteaapi.internal.nms.v1_20_R1;

import com.google.common.collect.Multimap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.ricetea.barleyteaapi.api.command.CommandRegister;
import org.ricetea.barleyteaapi.api.event.EntitiesRegisteredEvent;
import org.ricetea.barleyteaapi.api.event.EntitiesUnregisteredEvent;
import org.ricetea.barleyteaapi.api.event.ItemsRegisteredEvent;
import org.ricetea.barleyteaapi.api.event.ItemsUnregisteredEvent;
import org.ricetea.barleyteaapi.internal.nms.INBTItemHelper;
import org.ricetea.barleyteaapi.internal.nms.INMSEntityHelper;
import org.ricetea.barleyteaapi.internal.nms.INMSItemHelper;
import org.ricetea.barleyteaapi.internal.nms.NMSHelperRegister;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R1.command.CommandRegisterImpl;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R1.command.NMSGiveCommand;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R1.command.NMSRegularCommand;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R1.command.NMSSummonCommand;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R1.helper.NBTItemHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R1.helper.NBTTagCompoundHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R1.helper.NMSEntityHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R1.helper.NMSItemHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.logging.Logger;

public final class PluginEntryPoint extends JavaPlugin implements Listener {

    private static PluginEntryPoint _inst;

    private NMSRegularCommand summonCommand, giveCommand;

    @Nonnull
    public static PluginEntryPoint getInstance() {
        return Objects.requireNonNull(_inst);
    }

    @Nullable
    public static PluginEntryPoint getInstanceUnsafe() {
        return _inst;
    }

    @Override
    public void onEnable() {
        _inst = this;
        Logger logger = getLogger();
        logger.info("registering command register...");
        CommandRegisterImpl commandRegister = new CommandRegisterImpl();
        CommandRegister.setInstance(commandRegister, CommandRegisterImpl.class);
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
            public ItemStack copyNbt(@Nonnull ItemStack original, @Nonnull ItemStack itemStackCopying) {
                var compound = NBTItemHelper.getNBT(original);
                var compound2 = NBTItemHelper.getNBT(itemStackCopying);
                return NBTItemHelper.setNBT(original, NBTTagCompoundHelper.merge(compound2, compound));
            }
        }, INBTItemHelper.class);
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        Logger logger = getLogger();
        logger.info("unregistering command register...");
        CommandRegisterImpl commandRegister = CommandRegister.getInstanceUnsafe(CommandRegisterImpl.class);
        if (commandRegister != null) {
            commandRegister.unregisterAll();
            CommandRegister.setInstance(null, CommandRegisterImpl.class);
        }
        logger.info("unregistering nms function...");
        NMSHelperRegister.setHelper(null, INMSEntityHelper.class);
        NMSHelperRegister.setHelper(null, INMSItemHelper.class);
    }

    @EventHandler
    public void listenEntitiesRegistered(EntitiesRegisteredEvent event){
        if (event == null)
            return;
        summonCommand.updateSuggestions();
    }

    @EventHandler
    public void listenEntitiesUnregistered(EntitiesUnregisteredEvent event){
        if (event == null)
            return;
        summonCommand.updateSuggestions();
    }

    @EventHandler
    public void listenItemsRegistered(ItemsRegisteredEvent event){
        if (event == null)
            return;
        giveCommand.updateSuggestions();
    }

    @EventHandler
    public void listenItemsUnregistered(ItemsUnregisteredEvent event){
        if (event == null)
            return;
        giveCommand.updateSuggestions();
    }
}
