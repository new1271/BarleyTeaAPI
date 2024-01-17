package org.ricetea.barleyteaapi.api.item.feature.data;

import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.base.data.BaseItemHoldEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public final class DataItemHoldPlayerDeath extends BaseItemHoldEntityFeatureData<PlayerDeathEvent> {
    @Nullable
    private final Entity killer;

    @Nullable
    private final Lazy<CustomEntityType> killerType;

    public DataItemHoldPlayerDeath(@Nonnull PlayerDeathEvent event,
                                   @Nullable EntityDamageByEntityEvent lastDamageCauseByEntityEvent, @Nonnull ItemStack itemStack,
                                   @Nonnull EquipmentSlot equipmentSlot) {
        super(event, event.getEntity(), itemStack, equipmentSlot);
        killer = ObjectUtil.safeMap(lastDamageCauseByEntityEvent, EntityDamageByEntityEvent::getDamager);
        killerType = ObjectUtil.safeMap(killer, killer -> Lazy.create(() -> CustomEntityType.get(killer)));
    }

    @Override
    @Nonnull
    public Player getHolderEntity() {
        return Objects.requireNonNull(event.getEntity());
    }

    @Nullable
    public Entity getKiller() {
        return killer;
    }

    @Nullable
    public CustomEntityType getKillerType() {
        Lazy<CustomEntityType> killerType = this.killerType;
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

    public boolean getShouldPlayDeathSound() {
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

    @Nonnull
    public List<ItemStack> getItemsToKeep() {
        return event.getItemsToKeep();
    }

    @Nullable
    public Component getDeathMessage() {
        return event.deathMessage();
    }

    public void setDeathMessage(@Nullable Component deathMessage) {
        event.deathMessage(deathMessage);
    }

    public boolean shouldDropExperience() {
        return event.shouldDropExperience();
    }

    public void setShouldDropExperience(boolean doExpDrop) {
        event.setShouldDropExperience(doExpDrop);
    }

    public int getNewExp() {
        return event.getNewExp();
    }

    public void setNewExp(int exp) {
        event.setNewExp(exp);
    }

    public int getNewLevel() {
        return event.getNewLevel();
    }

    public void setNewLevel(int level) {
        event.setNewLevel(level);
    }

    public int getNewTotalExp() {
        return event.getNewTotalExp();
    }

    public void setNewTotalExp(int totalExp) {
        event.setNewTotalExp(totalExp);
    }

    public boolean getKeepLevel() {
        return event.getKeepLevel();
    }

    public void setKeepLevel(boolean keepLevel) {
        event.setKeepLevel(keepLevel);
    }

    public boolean getKeepInventory() {
        return event.getKeepInventory();
    }

    public void setKeepInventory(boolean keepInventory) {
        event.setKeepInventory(keepInventory);
    }
}
