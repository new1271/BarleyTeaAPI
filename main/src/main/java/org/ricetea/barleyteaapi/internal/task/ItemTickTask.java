package org.ricetea.barleyteaapi.internal.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.helper.FeatureHelper;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemTick;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.utils.Constants;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class ItemTickTask extends LoopTaskBase {
    @Nonnull
    private static final Lazy<ItemTickTask> _inst = Lazy.create(ItemTickTask::new);
    private int lastTick;

    private ItemTickTask() {
        super(50);
    }

    @Nullable
    public static ItemTickTask getInstanceUnsafe() {
        return _inst.getUnsafe();
    }

    @Nonnull
    public static ItemTickTask getInstance() {
        return _inst.get();
    }

    @Override
    public void runLoop() {
        BarleyTeaAPI api = BarleyTeaAPI.getInstanceUnsafe();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        ItemRegister register = ItemRegister.getInstanceUnsafe();
        if (api == null || register == null || register.findFirstOfFeature(FeatureItemTick.class) == null) {
            stop();
        } else {
            int currentTick = Bukkit.getCurrentTick();
            if (currentTick != lastTick) {
                lastTick = currentTick;
                Player[] players = Bukkit.getOnlinePlayers().toArray(Player[]::new);
                if (players != null) {
                    for (Player player : players) {
                        if (player != null && !player.isDead()) {
                            PlayerInventory inv = player.getInventory();
                            for (EquipmentSlot slot : Constants.ALL_SLOTS) {
                                if (slot != null && ItemHelper.isSuitableForPlayer(slot)) {
                                    ItemStack itemStack = inv.getItem(slot);
                                    FeatureItemTick feature = FeatureHelper.getFeatureUnsafe(
                                            CustomItem.get(itemStack), FeatureItemTick.class);
                                    if (feature != null) {
                                        scheduler.scheduleSyncDelayedTask(api,
                                                () -> feature.handleTickOnEquipment(player, inv,
                                                        itemStack, slot));
                                    }
                                }
                            }
                            for (int i = 0, count = inv.getSize(); i < count; i++) {
                                final int slot = i;
                                ItemStack itemStack = inv.getItem(slot);
                                FeatureItemTick feature = FeatureHelper.getFeatureUnsafe(
                                        CustomItem.get(itemStack), FeatureItemTick.class);
                                if (feature != null) {
                                    scheduler.scheduleSyncDelayedTask(api,
                                            () -> feature.handleTickOnInventory(player, inv,
                                                    itemStack, slot));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
