package org.ricetea.barleyteaapi.internal.task;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
import org.ricetea.barleyteaapi.util.Lazy;

public final class ItemTickTask extends AbstractTask {

    @Nonnull
    private static final Lazy<ItemTickTask> _inst = new Lazy<>(ItemTickTask::new);

    @Nonnull
    private static final EquipmentSlot[] SLOTS = EquipmentSlot.values();

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
        BarleyTeaAPI api = BarleyTeaAPI.getInstance();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        ItemRegister register = ItemRegister.getInstanceUnsafe();
        if (api == null || scheduler == null || register == null || !register.hasAnyRegisteredNeedTicking()) {
            stop();
        } else {
            Player[] players = Bukkit.getOnlinePlayers().toArray(Player[]::new);
            if (players != null) {
                for (Player player : players) {
                    if (player == null || player.isDead()) {
                        continue;
                    } else {
                        PlayerInventory inv = player.getInventory();
                        for (EquipmentSlot slot : SLOTS) {
                            if (slot != null) {
                                ItemStack itemStack = inv.getItem(slot);
                                if (itemStack != null) {
                                    NamespacedKey id = BaseItem.getItemID(itemStack);
                                    if (id != null
                                            && register.lookup(id) instanceof FeatureItemTick itemTickFeature) {
                                        scheduler.runTask(api, () -> itemTickFeature.handleTickOnEquipment(player, inv,
                                                itemStack, slot));
                                    }
                                }
                            }
                        }
                        ItemStack[] storage = inv.getStorageContents();
                        for (int i = 0, count = storage.length; i < count; i++) {
                            final int slot = i;
                            ItemStack itemStack = inv.getItem(slot);
                            if (itemStack != null) {
                                NamespacedKey id = BaseItem.getItemID(itemStack);
                                if (id != null
                                        && register.lookup(id) instanceof FeatureItemTick itemTickFeature) {
                                    scheduler.runTask(api, () -> itemTickFeature.handleTickOnInventory(player, inv,
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
