package org.ricetea.barleyteaapi.test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.*;
import org.ricetea.barleyteaapi.util.EntityUtils;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;

import net.kyori.adventure.text.Component;

public final class TestEntity extends BaseEntity // based on BarleyTeaAPI's Base Entity Class
        implements FeatureCommandSummon, FeatureEntityDamage, FeatureEntityDeath { // implements entity features

    private static final Lazy<TestEntity> inst = new Lazy<>(TestEntity::new);

    @Nonnull
    public static TestEntity getInstance() {
        return inst.get();
    }

    private TestEntity() {
        super(NamespacedKeyUtils.BarleyTeaAPI("test_entity"), EntityType.ZOMBIE);
    }

    @Override
    public boolean handleEntityDeath(DataEntityDeath data) {
        if (data.hasKiller()) {
            Entity killer = data.getKiller();
            if (killer instanceof Player) //if killer is player
                killer.sendMessage(data.getDecedent().name().append(Component.text(" is dead!")));
            else if (killer instanceof Projectile) { //if killer is arrow, fireball or something that is projectile
                killer = EntityUtils.getProjectileShooterEntity((Projectile) killer);
                if (killer != null && killer instanceof Player) //if killer is exist and killer is player
                    killer.sendMessage(data.getDecedent().name().append(Component.text(" is dead!")));
            }
        }
        return true; //accept entity death
    }

    @Override
    public boolean handleEntityDamagedByEntity(DataEntityDamagedByEntity data) {
        if (data.getDamager() instanceof Player) {
            data.getDamager().sendMessage(data.getDamagee().name()
                    .append(Component.text(" is dealed ")).append(Component.text(data.getDamage()))
                    .append(Component.text(" damage!")));
        }
        return true; //accept entity damaged by another entity
    }

    @Override
    public boolean handleEntityDamagedByBlock(DataEntityDamagedByBlock data) {
        return true; //accept entity damaged by block
    }

    @Override
    public boolean handleEntityDamagedByNothing(DataEntityDamagedByNothing data) {
        return true; //accept entity damaged by nothing(environment)
    }

    @Override
    public boolean handleEntityAttack(DataEntityDamagedByEntity data) {
        if (data.getDamagee() instanceof Player) {
            data.getDamagee().sendMessage(data.getDamager().name()
                    .append(Component.text(" damages you ")).append(Component.text(data.getFinalDamage()))
                    .append(Component.text(" !")));
        }
        return true; //accept entity attack another entity
    }

    @Override
    public boolean handleCommandSummon(@Nonnull Entity entitySummoned, @Nullable String nbt) {
        return true; //accept entity summon by command
    }

}
