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
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemSlotFilter;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemTick;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemSlotFilter;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.util.PlayerUtil;
import org.ricetea.utils.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Collection;

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

        if (api == null || register == null || !register.hasFeature(FeatureItemTick.class)) {
            stop();
            return;
        }

        int currentTick = Bukkit.getCurrentTick();
        if (currentTick == lastTick)
            return;
        lastTick = currentTick;

        Collection<? extends Player> players = PlayerUtil.getOnlinePlayerSnapshot();
        if (players.isEmpty())
            return;

        ChainedRunner runner = ChainedRunner.create();

        for (Player player : players) {
            if (player == null || player.isDead() || !player.isOnline())
                continue;
            PlayerInventory inv = player.getInventory();
            for (EquipmentSlot slot : Constants.ALL_SLOTS) {
                if (slot == null || !ItemHelper.isSuitableForPlayer(slot))
                    continue;
                ItemStack itemStack = inv.getItem(slot);
                CustomItem itemType = CustomItem.get(itemStack);
                if (itemType == null)
                    continue;
                FeatureItemTick feature = FeatureHelper.getFeatureUnsafe(itemType, FeatureItemTick.class);
                if (feature == null)
                    continue;
                FeatureItemSlotFilter filterFeature = FeatureHelper.getFeatureUnsafe(itemType, FeatureItemSlotFilter.class);
                if (filterFeature != null &&
                        !filterFeature.handleItemSlotFilter(new DataItemSlotFilter(itemStack, slot)))
                    continue;
                runner.attach(() -> feature.handleTickOnEquipment(player, inv,
                        itemStack, slot));
            }
            for (int i = 0, count = inv.getSize(); i < count; i++) {
                final int slot = i;
                ItemStack itemStack = inv.getItem(slot);
                CustomItem itemType = CustomItem.get(itemStack);
                if (itemType == null)
                    continue;
                FeatureItemTick feature = FeatureHelper.getFeatureUnsafe(itemType, FeatureItemTick.class);
                if (feature == null)
                    continue;
                runner.attach(() -> feature.handleTickOnInventory(player, inv,
                        itemStack, slot));
            }
        }
        runner.freeze().run(api, scheduler);
    }
}
