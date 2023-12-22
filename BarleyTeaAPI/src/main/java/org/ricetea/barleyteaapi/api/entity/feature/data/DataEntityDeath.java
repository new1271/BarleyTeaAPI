package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public final class DataEntityDeath extends BaseEntityFeatureData<EntityDeathEvent> {
    @Nullable
    private final Entity killer;

    @Nullable
    private final Lazy<DataEntityType> killerType;

    public DataEntityDeath(@Nonnull EntityDeathEvent event,
            @Nullable EntityDamageByEntityEvent lastDamageCauseByEntityEvent) {
        super(event);
        killer = ObjectUtil.safeMap(lastDamageCauseByEntityEvent, EntityDamageByEntityEvent::getDamager);
        killerType = ObjectUtil.safeMap(killer, killer -> Lazy.create(() -> BaseEntity.getEntityType(killer)));
    }

    @Nonnull
    public LivingEntity getEntity() {
        return Objects.requireNonNull(event.getEntity());
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

    public @Nonnull List<ItemStack> getDrops() {
        return Objects.requireNonNull(event.getDrops());
    }

    public double getReviveHealth() {
        return event.getReviveHealth();
    }

    public void setReviveHealth(double reviveHealth) throws IllegalArgumentException {
        event.setReviveHealth(reviveHealth);
    }

    public boolean shouldPlayDeathSound() {
        return event.shouldPlayDeathSound();
    }

    public void setShouldPlayDeathSound(boolean playDeathSound) {
        event.setShouldPlayDeathSound(playDeathSound);
    }

    public @Nullable Sound getDeathSound() {
        return event.getDeathSound();
    }

    public void setDeathSound(@Nullable Sound sound) {
        event.setDeathSound(sound);
    }

    public @Nullable SoundCategory getDeathSoundCategory() {
        return event.getDeathSoundCategory();
    }

    public void setDeathSoundCategory(@Nullable SoundCategory soundCategory) {
        event.setDeathSoundCategory(soundCategory);
    }

    public float getDeathSoundVolume() {
        return event.getDeathSoundVolume();
    }

    public void setDeathSoundVolume(float volume) {
        event.setDeathSoundVolume(volume);
    }

    public float getDeathSoundPitch() {
        return event.getDeathSoundPitch();
    }

    public void setDeathSoundPitch(float pitch) {
        event.setDeathSoundPitch(pitch);
    }
}
