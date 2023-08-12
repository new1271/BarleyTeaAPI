package org.ricetea.barleyteaapi.test;

import javax.annotation.Nonnull;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.ricetea.barleyteaapi.api.entity.SpawnableEntity;
import org.ricetea.barleyteaapi.api.entity.feature.*;
import org.ricetea.barleyteaapi.api.entity.feature.data.*;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.util.Lazy;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;

public final class TestEntity extends SpawnableEntity // based on BarleyTeaAPI's Spawnable Entity Class
        implements FeatureEntityDamage, FeatureEntityDeath, FeatureKillEntity { // implements entity features

    private static final Lazy<TestEntity> inst = new Lazy<>(TestEntity::new);

    @Nonnull
    public static TestEntity getInstance() {
        return inst.get();
    }

    private TestEntity() {
        //create a custom entity that base on zombie and can summon with command "/summonbarley testonly:test_entity"
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
                killer.sendMessage(data.getDecedent().name().append(Component.text(" is dead!"))
                        .style(Style.style(NamedTextColor.GOLD)));
            else if (killer instanceof Projectile) { //if killer is arrow, fireball or something that is projectile
                killer = EntityHelper.getProjectileShooterEntity((Projectile) killer);
                if (killer != null && killer instanceof Player) //if killer is exist and killer is player
                    killer.sendMessage(data.getDecedent().name()
                            .append(Component.text(" is dead!").style(Style.style(NamedTextColor.GOLD))));
            }
        }
        return true; //accept decedent(the entity) deaths
    }

    @Override
    public boolean handleEntityDamagedByEntity(DataEntityDamagedByEntity data) {
        if (data.getDamager() instanceof Player) {
            data.getDamager().sendMessage(data.getDamagee().name()
                    .append(Component.text(" is dealed ").style(Style.style(NamedTextColor.WHITE)))
                    .append(Component.text(data.getDamage()).style(Style.style(NamedTextColor.GOLD)))
                    .append(Component.text(" damage!")).style(Style.style(NamedTextColor.WHITE)));
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
                    .append(Component.text(" damages you ").style(Style.style(NamedTextColor.WHITE)))
                    .append(Component.text(data.getFinalDamage()).style(Style.style(NamedTextColor.GOLD)))
                    .append(Component.text(" !")).style(Style.style(NamedTextColor.WHITE)));
        }
        return true; //accept the entity attacks another entity
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void spawn(@Nonnull Entity entitySummoned) {
        //Named entity as "default"(the name that is getDefaultName() set), and set color&style as ChatColor.BLUE + ChatColor.BOLD
        setEntityName(entitySummoned, ChatColor.BLUE, ChatColor.BOLD);
    }

    @Override
    public boolean handleKillEntity(DataKillEntity data) {
        return true; //accept the decedent death
    }

    @Override
    public boolean handleKillPlayer(DataKillPlayer data) {
        Player decedent = data.getDecedent();
        decedent.sendMessage(Component.text("You're killed by ").style(Style.style(NamedTextColor.WHITE))
                .append(data.getKiller().name())
                .append(Component.text(" !").style(Style.style(NamedTextColor.WHITE))));
        return true; //accept the decedent death
    }
}
