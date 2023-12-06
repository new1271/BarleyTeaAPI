package org.ricetea.barleyteaapi.api.item.feature.data;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.abstracts.BaseItemHoldEntityFeatureData;

public final class DataItemHoldEntityKillPlayer extends BaseItemHoldEntityFeatureData<PlayerDeathEvent> {

    public DataItemHoldEntityKillPlayer(@Nonnull PlayerDeathEvent event,
            @Nonnull EntityDamageByEntityEvent lastDamageCauseByEntityEvent, @Nonnull ItemStack itemStack,
            @Nonnull EquipmentSlot equipmentSlot) {
        super(event, (LivingEntity) lastDamageCauseByEntityEvent.getDamager(), itemStack, equipmentSlot);
    }

    @Nonnull
    public Player getDecedent() {
        return event.getEntity();
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
