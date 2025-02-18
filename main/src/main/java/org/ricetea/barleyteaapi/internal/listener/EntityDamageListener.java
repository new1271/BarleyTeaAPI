package org.ricetea.barleyteaapi.internal.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.base.data.BaseFeatureData;
import org.ricetea.barleyteaapi.api.base.data.BaseItemHoldEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityDamage;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityAttack;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByBlock;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByNothing;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldEntityDamage;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityAttack;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDamagedByBlock;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDamagedByEntity;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDamagedByNothing;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.barleyteaapi.internal.linker.ItemFeatureLinker;
import org.ricetea.barleyteaapi.internal.listener.patch.EntityDamageListenerPatch;
import org.ricetea.utils.Constants;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

@Singleton
@ApiStatus.Internal
public final class EntityDamageListener implements Listener {

    private static final EntityDamageListener _inst = new EntityDamageListener();

    private final List<EntityDamageListenerPatch> patchList = new ArrayList<>();

    private EntityDamageListener() {
    }

    @Nonnull
    public static EntityDamageListener getInstance() {
        return _inst;
    }

    public void addPatch(@Nonnull EntityDamageListenerPatch patch) {
        patchList.add(patch);
    }

    public void removePatch(@Nonnull EntityDamageListenerPatch patch) {
        patchList.remove(patch);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void listenEntityDamageFirst(EntityDamageEvent event) {
        if (event == null || event.isCancelled())
            return;
        if (event instanceof EntityDamageByEntityEvent _event) {
            onEntityDamageByEntityFirst(_event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenEntityDamageLast(EntityDamageEvent event) {
        if (event == null || event.isCancelled())
            return;
        if (event instanceof EntityDamageByEntityEvent _event) {
            onEntityDamageByEntityLast(_event);
            return;
        }
        if (event instanceof EntityDamageByBlockEvent _event) {
            onEntityDamageByBlock(_event);
            return;
        }
        onEntityDamageByNothing(event);
    }

    public void onEntityDamageByEntityFirst(@Nonnull EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!forEachEquipmentCancellable(damager, event,
                DataItemHoldEntityAttack.class,
                FeatureItemHoldEntityDamage::handleItemHoldEntityAttack,
                DataItemHoldEntityAttack::new)) {
            event.setCancelled(true);
            return;
        }
        if (!doFeatureCancellable(damager, event, DataEntityAttack.class,
                FeatureEntityDamage::handleEntityAttack, DataEntityAttack::new)) {
            event.setCancelled(true);
            return;
        }
    }

    public void onEntityDamageByEntityLast(@Nonnull EntityDamageByEntityEvent event) {
        Entity damagee = event.getEntity();
        if (!forEachEquipmentCancellable(damagee, event,
                DataItemHoldEntityDamagedByEntity.class,
                FeatureItemHoldEntityDamage::handleItemHoldEntityDamagedByEntity,
                DataItemHoldEntityDamagedByEntity::new)) {
            event.setCancelled(true);
            return;
        }
        if (!doFeatureCancellable(damagee, event, DataEntityDamagedByEntity.class,
                FeatureEntityDamage::handleEntityDamagedByEntity, DataEntityDamagedByEntity::new)) {
            event.setCancelled(true);
        }
    }

    public void onEntityDamageByBlock(@Nonnull EntityDamageByBlockEvent event) {
        Entity damagee = event.getEntity();
        if (!forEachEquipmentCancellable(damagee, event,
                DataItemHoldEntityDamagedByBlock.class,
                FeatureItemHoldEntityDamage::handleItemHoldEntityDamagedByBlock,
                DataItemHoldEntityDamagedByBlock::new)) {
            event.setCancelled(true);
            return;
        }
        if (!doFeatureCancellable(damagee, event, DataEntityDamagedByBlock.class,
                FeatureEntityDamage::handleEntityDamagedByBlock, DataEntityDamagedByBlock::new)) {
            event.setCancelled(true);
        }
    }

    public void onEntityDamageByNothing(@Nonnull EntityDamageEvent event) {
        Entity damagee = event.getEntity();
        if (!forEachEquipmentCancellable(damagee, event,
                DataItemHoldEntityDamagedByNothing.class,
                FeatureItemHoldEntityDamage::handleItemHoldEntityDamagedByNothing,
                DataItemHoldEntityDamagedByNothing::new)) {
            event.setCancelled(true);
            return;
        }
        if (!doFeatureCancellable(damagee, event, DataEntityDamagedByNothing.class,
                FeatureEntityDamage::handleEntityDamagedByNothing, DataEntityDamagedByNothing::new)) {
            event.setCancelled(true);
        }
    }

    private <TEvent extends Event, TData extends BaseItemHoldEntityFeatureData<TEvent>> boolean forEachEquipmentCancellable(
            @Nonnull Entity entity, @Nonnull TEvent event,
            @Nonnull Class<TData> dataClass,
            @Nonnull BiPredicate<FeatureItemHoldEntityDamage, TData> featureFunc,
            @Nonnull ItemFeatureLinker.ItemDataConstructorForEquipment<TEvent, TData> dataConstructorByDefault) {
        if (!(entity instanceof LivingEntity livingEntity) || !ItemRegister.hasRegistered())
            return true;
        ItemFeatureLinker.ItemDataConstructorForEquipment<TEvent, TData> constructor =
                patchList.stream()
                        .map(_patch -> _patch.getItemHoldEntityEventDataFactoryOrNull(dataClass))
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(dataConstructorByDefault);
        return ItemFeatureLinker.forEachEquipmentCancellable(livingEntity, event, Constants.ALL_SLOTS,
                FeatureItemHoldEntityDamage.class, featureFunc, constructor);
    }

    private <TEvent extends Event, TData extends BaseFeatureData<TEvent>> boolean doFeatureCancellable(
            @Nonnull Entity entity, @Nonnull TEvent event,
            @Nonnull Class<TData> dataClass,
            @Nonnull BiPredicate<FeatureEntityDamage, TData> featureFunc,
            @Nonnull Function<TEvent, TData> dataConstructorByDefault) {
        if (!EntityRegister.hasRegistered())
            return true;
        Function<TEvent, TData> constructor =
                patchList.stream()
                        .map(_patch -> _patch.getEntityEventDataFactoryOrNull(dataClass))
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(dataConstructorByDefault);
        return EntityFeatureLinker.doFeatureCancellable(entity, event,
                FeatureEntityDamage.class, featureFunc, constructor);
    }
}
