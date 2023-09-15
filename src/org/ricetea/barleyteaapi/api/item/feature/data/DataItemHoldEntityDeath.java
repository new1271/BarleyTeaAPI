package org.ricetea.barleyteaapi.api.item.feature.data;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseItemHoldEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class DataItemHoldEntityDeath extends BaseItemHoldEntityFeatureData<EntityDeathEvent> {
    @Nullable
    private final Entity killer;

    @Nullable
    private final Lazy<DataEntityType> killerType;

    @Nonnull
    private final Lazy<DataEntityType> decedentType;

    @SuppressWarnings("null")
    public DataItemHoldEntityDeath(@Nonnull EntityDeathEvent event,
            @Nullable EntityDamageByEntityEvent lastDamageCauseByEntityEvent, @Nonnull ItemStack itemStack,
            @Nonnull EquipmentSlot equipmentSlot) {
        super(event, event.getEntity(), itemStack, equipmentSlot);
        decedentType = new Lazy<>(() -> BaseEntity.getEntityType(getDecedent()));
        killer = ObjectUtil.mapWhenNonnull(lastDamageCauseByEntityEvent, EntityDamageByEntityEvent::getDamager);
        killerType = ObjectUtil.mapWhenNonnull(killer, killer -> new Lazy<>(() -> BaseEntity.getEntityType(killer)));
    }

    @Nonnull
    public LivingEntity getDecedent() {
        return Objects.requireNonNull(event.getEntity());
    }

    @Nonnull
    public DataEntityType getDecedentType() {
        return decedentType.get();
    }

    @Nullable
    public Entity getKiller() {
        return killer;
    }

    @Nullable
    public DataEntityType getKillerType() {
        Lazy<DataEntityType> killerType = this.killerType;
        if (killerType == null)
            return null;
        else
            return killerType.get();
    }

    public boolean hasKiller() {
        return killer != null;
    }

    public int getDroppedExp() {
        return event.getDroppedExp();
    }

    public void setDroppedExp(int exp) {
        event.setDroppedExp(exp);
    }

    public @Nonnull List<ItemStack> getDecedentDrops() {
        return Objects.requireNonNull(event.getDrops());
    }

    public double getDecedentReviveHealth() {
        return event.getReviveHealth();
    }

    public void setDecedentReviveHealth(double reviveHealth) throws IllegalArgumentException {
        event.setReviveHealth(reviveHealth);
    }

    public boolean getDecedentShouldPlayDeathSound() {
        return event.shouldPlayDeathSound();
    }

    public void setDecedentShouldPlayDeathSound(boolean playDeathSound) {
        event.setShouldPlayDeathSound(playDeathSound);
    }

    public @Nullable Sound getDecedentDeathSound() {
        return event.getDeathSound();
    }

    public void setDecedentDeathSound(@Nullable Sound sound) {
        event.setDeathSound(sound);
    }

    public @Nullable SoundCategory getDecedentDeathSoundCategory() {
        return event.getDeathSoundCategory();
    }

    public void setDecedentDeathSoundCategory(@Nullable SoundCategory soundCategory) {
        event.setDeathSoundCategory(soundCategory);
    }

    public float getDecedentDeathSoundVolume() {
        return event.getDeathSoundVolume();
    }

    public void setDecedentDeathSoundVolume(float volume) {
        event.setDeathSoundVolume(volume);
    }

    public float getDecedentDeathSoundPitch() {
        return event.getDeathSoundPitch();
    }

    public void setDecedentDeathSoundPitch(float pitch) {
        event.setDeathSoundPitch(pitch);
    }
}
