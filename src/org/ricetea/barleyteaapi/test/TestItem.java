package org.ricetea.barleyteaapi.test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.ricetea.barleyteaapi.api.item.BaseItem;
import org.ricetea.barleyteaapi.api.item.data.DataItemRarity;
import org.ricetea.barleyteaapi.api.item.feature.FeatureCommandGive;
import org.ricetea.barleyteaapi.api.item.feature.FeatureCustomDurability;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldEntityDamage;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldEntityKill;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityAttack;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDamagedByBlock;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDamagedByEntity;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityDamagedByNothing;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityKillEntity;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityKillPlayer;
import org.ricetea.barleyteaapi.util.Lazy;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class TestItem extends BaseItem
        implements FeatureCommandGive, FeatureItemHoldEntityDamage, FeatureItemHoldEntityKill, FeatureCustomDurability {

    private static final Lazy<TestItem> inst = new Lazy<>(TestItem::new);

    @Nonnull
    public static TestItem getInstance() {
        return inst.get();
    }

    private TestItem() {
        //create a custom item that base on diamond sword and can give player with command "/givebarley <playerID> testonly:test_item"
        super(new NamespacedKey("testonly", "test_item"), Material.DIAMOND_SWORD, DataItemRarity.EPIC);
    }

    @Override
    @Nonnull
    public String getDefaultName() { //set default item name
        return "Test Item";
    }

    @Override
    public boolean handleCommandGive(@Nonnull ItemStack itemStackGived, @Nullable String nbt) {
        setItemName(itemStackGived);
        return true;
    }

    @Override
    public boolean handleItemHoldEntityKillEntity(@Nonnull DataItemHoldEntityKillEntity data) {
        data.getHolderEntity()
                .sendMessage(Component.text("你已殺死 ").append(data.getDecedent().name()).append(Component.text("!")));
        return true;
    }

    @Override
    public boolean handleItemHoldEntityKillPlayer(@Nonnull DataItemHoldEntityKillPlayer data) {
        data.getHolderEntity()
                .sendMessage(Component.text("你已殺死 ").append(data.getDecedent().name()).append(Component.text("!")));
        return true;
    }

    @Override
    public boolean handleItemHoldEntityDamagedByEntity(@Nonnull DataItemHoldEntityDamagedByEntity data) {
        return true;
    }

    @Override
    public boolean handleItemHoldEntityDamagedByBlock(@Nonnull DataItemHoldEntityDamagedByBlock data) {
        return true;
    }

    @Override
    public boolean handleItemHoldEntityDamagedByNothing(@Nonnull DataItemHoldEntityDamagedByNothing data) {
        return true;
    }

    @Override
    public boolean handleItemHoldEntityAttack(@Nonnull DataItemHoldEntityAttack data) {
        data.getHolderEntity()
                .sendMessage(Component.text("你對 ", NamedTextColor.WHITE)
                        .append(data.getDamagee().name())
                        .append(Component.text(" 造成了 ", NamedTextColor.WHITE))
                        .append(Component.text(data.getFinalDamage(), NamedTextColor.GOLD))
                        .append(Component.text(" 點傷害", NamedTextColor.WHITE)));
        return true;
    }

    @Override
    public int getMaxDurability(@Nonnull ItemStack itemStack) {
        return 20;
    }

}
