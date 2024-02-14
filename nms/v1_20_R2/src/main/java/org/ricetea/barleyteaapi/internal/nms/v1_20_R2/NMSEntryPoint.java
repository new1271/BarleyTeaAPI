package org.ricetea.barleyteaapi.internal.nms.v1_20_R2;

import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.ServicesManager;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.command.CommandRegister;
import org.ricetea.barleyteaapi.api.event.EntitiesRegisteredEvent;
import org.ricetea.barleyteaapi.api.event.EntitiesUnregisteredEvent;
import org.ricetea.barleyteaapi.api.event.ItemsRegisteredEvent;
import org.ricetea.barleyteaapi.api.event.ItemsUnregisteredEvent;
import org.ricetea.barleyteaapi.api.internal.nms.INBTItemHelper;
import org.ricetea.barleyteaapi.api.internal.nms.INMSEntityHelper;
import org.ricetea.barleyteaapi.api.internal.nms.INMSEntryPoint;
import org.ricetea.barleyteaapi.api.internal.nms.INMSItemHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R2.command.NMSCommandRegisterImpl;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R2.command.NMSGiveCommand;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R2.command.NMSRegularCommand;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R2.command.NMSSummonCommand;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R2.helper.NBTItemHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R2.helper.NBTTagCompoundHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R2.helper.NMSEntityHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R2.helper.NMSItemHelper;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
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
        apiInst.loadApiImplementation(servicesManager, new INMSItemHelper() {
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
        apiInst.loadApiImplementation(servicesManager, new INBTItemHelper() {
            @Nonnull
            @Override
            public ItemStack copyNbt(@Nonnull ItemStack original, @Nonnull ItemStack result,
                                     @Nullable String... tagBlacklist) {
                var compoundOfOriginal = NBTItemHelper.getNBT(original);
                var compoundOfResult = NBTItemHelper.getNBT(result);
                if (tagBlacklist != null && tagBlacklist.length > 0) {
                    compoundOfOriginal = compoundOfOriginal.copy();
                    for (String tag : tagBlacklist) {
                        var currentCompoundNode = compoundOfOriginal;
                        String[] paths = tag.split("\\.");
                        for (int i = 0, length = paths.length; i < length; i++) {
                            if (currentCompoundNode == null)
                                break;
                            String path = paths[i];
                            if (i == length - 1) {
                                currentCompoundNode.remove(path);
                                break;
                            } else if (currentCompoundNode.contains(path)) {
                                currentCompoundNode = ObjectUtil.tryCast(currentCompoundNode.get(path), CompoundTag.class);
                            } else {
                                currentCompoundNode = null;
                            }
                        }
                    }
                }
                return NBTItemHelper.setNBT(result, NBTTagCompoundHelper.merge(compoundOfResult, compoundOfOriginal));
            }

            @Nonnull
            @Override
            public ItemStack mergeNbt(@Nonnull ItemStack original, @Nonnull ItemStack result, @Nullable String... tags) {
                var compoundOfOriginal = NBTItemHelper.getNBT(original);
                var compoundOfResult = NBTItemHelper.getNBT(result);
                if (tags != null && tags.length > 0) {
                    compoundOfOriginal = compoundOfOriginal.copy();
                    for (String tag : tags) {
                        var currentCompoundNodeOfOriginal = compoundOfOriginal;
                        var currentCompoundNodeOfResult = compoundOfResult;
                        String[] paths = tag.split("\\.");
                        for (int i = 0, length = paths.length; i < length; i++) {
                            if (currentCompoundNodeOfOriginal == null || currentCompoundNodeOfResult == null)
                                break;
                            String path = paths[i];
                            if (i == length - 1) {
                                merge(currentCompoundNodeOfResult, currentCompoundNodeOfOriginal);
                                break;
                            } else if (currentCompoundNodeOfOriginal.contains(path)) {
                                currentCompoundNodeOfOriginal = ObjectUtil.tryCast(currentCompoundNodeOfOriginal.get(path), CompoundTag.class);
                                currentCompoundNodeOfResult = ObjectUtil.tryCast(currentCompoundNodeOfResult.get(path), CompoundTag.class);
                            } else {
                                currentCompoundNodeOfOriginal = null;
                                currentCompoundNodeOfResult = null;
                            }
                        }
                    }
                    return NBTItemHelper.setNBT(result, compoundOfResult);
                }
                return result;
            }

            private void merge(@Nonnull CompoundTag node, @Nonnull CompoundTag node2) {
                for (String tagKey : node2.getAllKeys()) {
                    Tag tag = node.get(tagKey);
                    if (tag == null) {
                        node.put(tagKey, Objects.requireNonNull(node2.get(tagKey)));
                    } else if (tag.getId() == 10 && tag instanceof CompoundTag compoundTag &&
                            node2.contains(tagKey, 10)) { //Compound Type
                        merge(compoundTag, node2.getCompound(tagKey));
                    }
                }
            }
        }, INBTItemHelper.class);
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
