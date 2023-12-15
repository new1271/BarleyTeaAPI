package org.ricetea.barleyteaapi.api.item.feature.data;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseItemHoldEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

public final class DataItemHoldEntityShoot extends BaseItemHoldEntityFeatureData<ProjectileLaunchEvent> {

    @Nonnull
    private final Lazy<DataEntityType> entityType;

    public DataItemHoldEntityShoot(@Nonnull ProjectileLaunchEvent event, @Nonnull ItemStack itemStack,
            @Nonnull EquipmentSlot equipmentSlot) {
        super(event, Objects.requireNonNull(ObjectUtil.tryCast(event.getEntity().getShooter(), LivingEntity.class)),
                itemStack, equipmentSlot);
        entityType = Lazy.create(() -> BaseEntity.getEntityType(getProjectile()));
    }

    @Nonnull
    public Projectile getProjectile() {
        return event.getEntity();
    }

    @Nonnull
    public DataEntityType getProjectileType() {
        return entityType.get();
    }
}
