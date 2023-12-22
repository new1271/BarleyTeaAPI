package org.ricetea.barleyteaapi.internal.task;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemTick;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.api.task.AbstractTask;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ItemTickTask extends AbstractTask {

    @Nonnull
    private static final Lazy<ItemTickTask> _inst = Lazy.create(ItemTickTask::new);

    @Nonnull
    public static final EquipmentSlot[] SLOTS = EquipmentSlot.values();

    private int lastTick;

    private ItemTickTask() {
        super(50, 0);
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
    protected void runInternal() {
        BarleyTeaAPI api = BarleyTeaAPI.getInstanceUnsafe();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        ItemRegister register = ItemRegister.getInstanceUnsafe();
        if (api == null || register == null || !register.hasAnyRegisteredNeedTicking()) {
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
                            for (EquipmentSlot slot : SLOTS) {
                                if (slot != null) {
                                    ItemStack itemStack = inv.getItem(slot);
                                    NamespacedKey id = BaseItem.getItemID(itemStack);
                                    if (id != null
                                            && register.lookup(id) instanceof FeatureItemTick itemTickFeature) {
                                        scheduler.scheduleSyncDelayedTask(api,
                                                () -> itemTickFeature.handleTickOnEquipment(player, inv,
                                                        itemStack, slot));
                                    }
                                }
                            }
                            for (int i = 0, count = inv.getSize(); i < count; i++) {
                                final int slot = i;
                                ItemStack itemStack = inv.getItem(slot);
                                if (itemStack != null) {
                                    NamespacedKey id = BaseItem.getItemID(itemStack);
                                    if (id != null
                                            && register.lookup(id) instanceof FeatureItemTick itemTickFeature) {
                                        scheduler.scheduleSyncDelayedTask(api,
                                                () -> itemTickFeature.handleTickOnInventory(player, inv,
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
}
