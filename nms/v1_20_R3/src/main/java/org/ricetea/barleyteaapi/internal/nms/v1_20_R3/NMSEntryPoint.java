package org.ricetea.barleyteaapi.internal.nms.v1_20_R3;

import com.google.common.collect.Multimap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_20_R3.CraftEquipmentSlot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.ServicesManager;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.command.CommandRegister;
import org.ricetea.barleyteaapi.api.event.EntitiesRegisteredEvent;
import org.ricetea.barleyteaapi.api.event.EntitiesUnregisteredEvent;
import org.ricetea.barleyteaapi.api.event.ItemsRegisteredEvent;
import org.ricetea.barleyteaapi.api.event.ItemsUnregisteredEvent;
import org.ricetea.barleyteaapi.api.internal.nms.*;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R3.command.NMSCommandRegisterImpl;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R3.command.NMSGiveCommand;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R3.command.NMSRegularCommand;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R3.command.NMSSummonCommand;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R3.helper.NBTItemHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R3.helper.NBTTagCompoundHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R3.helper.NMSEntityHelper;
import org.ricetea.barleyteaapi.internal.nms.v1_20_R3.helper.NMSItemHelper;
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

    @Override
    public NMSVersion getNMSVersion() {
        return NMSVersion.v1_20_R3;
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
            public ItemStack createItemStackFromShowItem(@Nonnull HoverEvent.ShowItem showItem) {
                Key key = showItem.item();
                Item item = BuiltInRegistries.ITEM.getOptional(new ResourceLocation(key.namespace(), key.value()))
                        .orElse(null);
                if (item == null)
                    return null;
                if (item instanceof AirItem)
                    return new ItemStack(Material.AIR);
                net.minecraft.world.item.ItemStack nmsItemStack = new net.minecraft.world.item.ItemStack(item, showItem.count());
                String nbtString = ObjectUtil.safeMap(showItem.nbt(), BinaryTagHolder::string);
                if (nbtString != null) {
                    try {
                        nmsItemStack.setTag(TagParser.parseTag(nbtString));
                    } catch (Exception ignored) {
                    }
                }
                return nmsItemStack.asBukkitMirror();
            }

            @Nullable
            @Override
            public String getNMSEquipmentSlotName(@Nullable EquipmentSlot slot) {
                if (slot == null)
                    return null;
                return CraftEquipmentSlot.getNMS(slot).getName();
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

            @Nonnull
            @Override
            public ItemStack setNbt(@Nonnull ItemStack original, @Nonnull String nbt) {
                try {
                    return NBTItemHelper.setNBT(original, TagParser.parseTag(nbt));
                } catch (CommandSyntaxException ignored) {
                    return original;
                }
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
