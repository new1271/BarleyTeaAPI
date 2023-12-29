package org.ricetea.barleyteaapi.internal.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemTick;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.utils.Constants;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
        if (api == null || !ItemRegister.hasRegisteredNeedTicking()) {
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
                                if (slot != null) {
                                    ItemStack itemStack = inv.getItem(slot);
                                    CustomItem itemType = CustomItem.get(itemStack);
                                    if (itemType instanceof FeatureItemTick feature) {
                                        scheduler.scheduleSyncDelayedTask(api,
                                                () -> feature.handleTickOnEquipment(player, inv,
                                                        itemStack, slot));
                                    }
                                }
                            }
                            for (int i = 0, count = inv.getSize(); i < count; i++) {
                                final int slot = i;
                                ItemStack itemStack = inv.getItem(slot);
                                CustomItem itemType = CustomItem.get(itemStack);
                                if (itemType instanceof FeatureItemTick feature) {
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
