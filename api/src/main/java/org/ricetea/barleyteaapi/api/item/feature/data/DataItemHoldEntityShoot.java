package org.ricetea.barleyteaapi.api.item.feature.data;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.data.BaseItemHoldEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataItemHoldEntityShoot extends BaseItemHoldEntityFeatureData<ProjectileLaunchEvent> {

    @Nonnull
    private final Lazy<CustomEntityType> entityType;

    public DataItemHoldEntityShoot(@Nonnull ProjectileLaunchEvent event, @Nonnull ItemStack itemStack,
                                   @Nonnull EquipmentSlot equipmentSlot) {
        super(event, Objects.requireNonNull(ObjectUtil.tryCast(event.getEntity().getShooter(), LivingEntity.class)),
                itemStack, equipmentSlot);
        entityType = Lazy.create(() -> CustomEntityType.get(getProjectile()));
    }

    @Nonnull
    public Projectile getProjectile() {
        return event.getEntity();
    }

    @Nonnull
    public CustomEntityType getProjectileType() {
        return entityType.get();
    }
}
