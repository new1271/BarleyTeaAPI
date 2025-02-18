package org.ricetea.barleyteaapi.internal.v2.listener.patch;

import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.data.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.base.data.BaseFeatureData;
import org.ricetea.barleyteaapi.api.base.data.BaseItemHoldEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.feature.data.*;
import org.ricetea.barleyteaapi.api.item.feature.data.*;
import org.ricetea.barleyteaapi.internal.linker.ItemFeatureLinker.ItemDataConstructorForEquipment;
import org.ricetea.barleyteaapi.internal.listener.patch.EntityDamageListenerPatch;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Hashtable;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public final class EntityDamageListenerPatchImpl implements EntityDamageListenerPatch {

    private static final EntityDamageListenerPatchImpl _inst = new EntityDamageListenerPatchImpl();

    private final Hashtable<Class<?>, ItemDataConstructorForEquipment<?, ?>> itemDataFactoryTable = new Hashtable<>();
    private final Hashtable<Class<?>, Function<?, ?>> entityDataFactoryTable = new Hashtable<>();

    private EntityDamageListenerPatchImpl() {
        itemDataFactoryTable.put(DataItemHoldEntityAttack.class,
                new ItemDataConstructorForEquipmentWrapper<>(DataItemHoldEntityAttackV2::new));
        itemDataFactoryTable.put(DataItemHoldEntityDamagedByBlock.class,
                new ItemDataConstructorForEquipmentWrapper<>(DataItemHoldEntityDamagedByBlockV2::new));
        itemDataFactoryTable.put(DataItemHoldEntityDamagedByEntity.class,
                new ItemDataConstructorForEquipmentWrapper<>(DataItemHoldEntityDamagedByEntityV2::new));
        itemDataFactoryTable.put(DataItemHoldEntityDamagedByNothing.class,
                new ItemDataConstructorForEquipmentWrapper<>(DataItemHoldEntityDamagedByNothingV2::new));
        entityDataFactoryTable.put(DataEntityAttack.class,
                new EntityDataConstructorWrapper<>(DataEntityAttackV2::new));
        entityDataFactoryTable.put(DataEntityDamagedByBlock.class,
                new EntityDataConstructorWrapper<>(DataEntityDamagedByBlockV2::new));
        entityDataFactoryTable.put(DataEntityDamagedByEntity.class,
                new EntityDataConstructorWrapper<>(DataEntityDamagedByEntityV2::new));
        entityDataFactoryTable.put(DataEntityDamagedByNothing.class,
                new EntityDataConstructorWrapper<>(DataEntityDamagedByNothingV2::new));
    }

    @Nonnull
    public static EntityDamageListenerPatchImpl getInstance() {
        return _inst;
    }

    @Nullable
    @Override
    public <TEvent extends Event, TData extends BaseFeatureData<TEvent>>
    Function<TEvent, TData> getEntityEventDataFactoryOrNull(
            @Nonnull Class<TData> dataClazz) {
        return (Function<TEvent, TData>) entityDataFactoryTable.get(dataClazz);
    }

    @Nullable
    @Override
    public <TEvent extends Event, TData extends BaseItemHoldEntityFeatureData<TEvent>>
    ItemDataConstructorForEquipment<TEvent, TData> getItemHoldEntityEventDataFactoryOrNull(
            @Nonnull Class<TData> dataClazz) {
        return (ItemDataConstructorForEquipment<TEvent, TData>) itemDataFactoryTable.get(dataClazz);
    }

    private static final class ItemDataConstructorForEquipmentWrapper<
            TEvent extends Event, TData extends BaseItemHoldEntityFeatureData<TEvent>> implements
            ItemDataConstructorForEquipment<TEvent, TData> {
        private final ItemDataConstructorForEquipment<TEvent, ? extends TData> _factory;

        private ItemDataConstructorForEquipmentWrapper(
                @Nonnull ItemDataConstructorForEquipment<TEvent, ? extends TData> factory) {
            _factory = factory;
        }

        @Nonnull
        @Override
        public TData apply(@Nonnull TEvent event, @Nonnull ItemStack itemStack, @Nonnull EquipmentSlot equipmentSlot) {
            return _factory.apply(event, itemStack, equipmentSlot);
        }
    }

    private static final class EntityDataConstructorWrapper<
            TEvent extends EntityEvent, TData extends BaseEntityFeatureData<TEvent>> implements
            Function<TEvent, TData> {
        private final Function<TEvent, ? extends TData> _factory;

        private EntityDataConstructorWrapper(
                @Nonnull Function<TEvent, ? extends TData> factory) {
            _factory = factory;
        }

        @Override
        public TData apply(TEvent event) {
            return _factory.apply(event);
        }
    }
}
