package org.ricetea.barleyteaapi.test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.*;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByBlock;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDamagedByNothing;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDeath;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataKillPlayer;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.util.Lazy;

import net.kyori.adventure.text.Component;

public final class TestEntity extends BaseEntity // based on BarleyTeaAPI's Base Entity Class
        implements FeatureCommandSummon, FeatureEntityDamage, FeatureEntityDeath, FeatureKillPlayer { // implements entity features

    private static final Lazy<TestEntity> inst = new Lazy<>(TestEntity::new);

    @Nonnull
    public static TestEntity getInstance() {
        return inst.get();
    }

    private TestEntity() {
        //create a custom entity that base on zombie and can spawn with "/summonbarley testonly:test_entity"
        super(new NamespacedKey("testonly", "test_entity"), EntityType.ZOMBIE);
    }

    @Override
    @Nonnull
    public String getDefaultName() { //set default display name for /summonbarley command's result in chat bar
        return "Test Entity";
    }

    @Override
    public boolean handleEntityDeath(DataEntityDeath data) {
        if (data.hasKiller()) {
            Entity killer = data.getKiller();
            if (killer instanceof Player) //if killer is player
                killer.sendMessage(data.getDecedent().name().append(Component.text(" is dead!")));
            else if (killer instanceof Projectile) { //if killer is arrow, fireball or something that is projectile
                killer = EntityHelper.getProjectileShooterEntity((Projectile) killer);
                if (killer != null && killer instanceof Player) //if killer is exist and killer is player
                    killer.sendMessage(data.getDecedent().name().append(Component.text(" is dead!")));
            }
        }
        return true; //accept decedent(the entity) deaths
    }

    @Override
    public boolean handleEntityDamagedByEntity(DataEntityDamagedByEntity data) {
        if (data.getDamager() instanceof Player) {
            data.getDamager().sendMessage(data.getDamagee().name()
                    .append(Component.text(" is dealed ")).append(Component.text(data.getDamage()))
                    .append(Component.text(" damage!")));
        }
        return true; //accept the entity is damaged by another entity
    }

    @Override
    public boolean handleEntityDamagedByBlock(DataEntityDamagedByBlock data) {
        return true; //accept the entity is damaged by block
    }

    @Override
    public boolean handleEntityDamagedByNothing(DataEntityDamagedByNothing data) {
        return true; //accept the entity is damaged by nothing(environment)
    }

    @Override
    public boolean handleEntityAttack(DataEntityDamagedByEntity data) {
        if (data.getDamagee() instanceof Player) {
            data.getDamagee().sendMessage(data.getDamager().name()
                    .append(Component.text(" damages you ")).append(Component.text(data.getFinalDamage()))
                    .append(Component.text(" !")));
        }
        return true; //accept the entity attacks another entity
    }

    @Override
    public boolean handleCommandSummon(@Nonnull Entity entitySummoned, @Nullable String nbt) {
        //Named entity as "default"(the name that is getDefaultName() set), and set color&style as ChatColor.BLUE + ChatColor.BOLD
        setEntityName(entitySummoned, ChatColor.BLUE, ChatColor.BOLD);
        return true; //accept the entity summoned by command
    }

    @Override
    public boolean handleKillPlayer(DataKillPlayer data) {
        Player decedent = data.getDecedent();
        decedent.sendMessage(Component.text("You're killed by ").append(data.getKiller().name())
                .append(Component.text(" !")));
        return true; //accept the decedent death
    }
}
