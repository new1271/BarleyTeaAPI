package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.data.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public final class DataKillEntity extends BaseEntityFeatureData<EntityDeathEvent> {
    @Nonnull
    private final Lazy<CustomEntityType> decedentType;

    public DataKillEntity(@Nonnull EntityDeathEvent event,
                          @Nonnull EntityDamageByEntityEvent lastDamageCauseByEntityEvent) {
        super(event, lastDamageCauseByEntityEvent.getDamager());
        decedentType = Lazy.create(() -> CustomEntityType.get(getDecedent()));
    }

    @Nonnull
    public LivingEntity getDecedent() {
        return Objects.requireNonNull(event.getEntity());
    }

    @Nonnull
    public CustomEntityType getDecedentType() {
        return decedentType.get();
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
