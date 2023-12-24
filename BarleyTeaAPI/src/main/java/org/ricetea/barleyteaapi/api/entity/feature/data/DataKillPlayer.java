package org.ricetea.barleyteaapi.api.entity.feature.data;

import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.data.BaseEntityFeatureData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public final class DataKillPlayer extends BaseEntityFeatureData<PlayerDeathEvent> {
    public DataKillPlayer(@Nonnull PlayerDeathEvent event,
                          @Nonnull EntityDamageByEntityEvent lastDamageCauseByEntityEvent) {
        super(event, lastDamageCauseByEntityEvent.getDamager());
    }

    @Nonnull
    public Player getDecedent() {
        return event.getEntity();
    }

    @Nullable
    public Component deathMessage() {
        return event.deathMessage();
    }

    public void deathMessage(@Nullable Component component) {
        event.deathMessage(component);
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
