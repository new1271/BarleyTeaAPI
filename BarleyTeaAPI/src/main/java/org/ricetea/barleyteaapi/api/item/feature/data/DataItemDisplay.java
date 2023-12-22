package org.ricetea.barleyteaapi.api.item.feature.data;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public final class DataItemDisplay {

    @Nullable
    private final Player player;

    @Nonnull
    private final ItemStack itemStack;

    @Nullable
    private Component displayName;

    @Nonnull
    private final List<Component> lore;

    public DataItemDisplay(@Nullable Player player, @Nonnull ItemStack itemStack, @Nullable Component displayName,
            @Nonnull List<Component> lore) {
        this.player = player;
        this.itemStack = itemStack;
        this.displayName = displayName;
        this.lore = lore;
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    @Nonnull
    public ItemStack getItemStack() {
        return itemStack;
    }

    @Nullable
    public Component getDisplayName() {
        return displayName;
    }

    @Nonnull
    public List<Component> getLore() {
        return lore;
    }

    public void setDisplayName(@Nullable Component displayName) {
        this.displayName = displayName;
    }
}
