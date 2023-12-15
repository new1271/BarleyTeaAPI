package org.ricetea.barleyteaapi.internal.nms.v1_20_R1.helper;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.WitherSkull;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Trident;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class NMSEntityHelper {

    public static void dropItem(org.bukkit.entity.Player player, org.bukkit.inventory.ItemStack item) {
        //net.minecraft.world.entity.item.ItemEntity drop(net.minecraft.world.item.ItemStack,boolean,boolean) -> a
        ((CraftPlayer) player).getHandle().drop(CraftItemStack.asNMSCopy(item), true, true);
    }

    public static net.minecraft.world.entity.Entity getNmsEntity(@Nullable org.bukkit.entity.Entity entity) {
        if (entity == null)
            return null;
        return ((CraftEntity) entity).getHandle();
    }

    public static boolean damage(@Nonnull org.bukkit.entity.Entity damagee, @Nullable org.bukkit.entity.Entity damager,
                                 @Nullable DamageCause cause, float damage, boolean withoutScaling) {
        net.minecraft.world.entity.Entity nmsDamagee = getNmsEntity(damagee);
        net.minecraft.world.entity.Entity nmsDamager = getNmsEntity(damager);
        var world = nmsDamagee.level();
        DamageSource damageSource = buildDamageSource(world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE),
                toDamageTypeResourceKey(cause, nmsDamager),
                nmsDamager, withoutScaling);
        return nmsDamagee.hurt(damageSource, damage);
    }

    @Nonnull
    private static DamageSource buildDamageSource(HolderLookup.RegistryLookup<DamageType> registry, ResourceKey<DamageType> key,
                                                  @Nullable Entity attacker, boolean withoutScaling) {
        return buildDamageSource(registry.getOrThrow(key), attacker, withoutScaling);
    }

    @Nonnull
    private static DamageSource buildDamageSource(Holder<DamageType> holder,
                                                  @Nullable Entity attacker, boolean withoutScaling) {
        if (withoutScaling) {
            DamageType oldDamageType = holder.value();
            if (!oldDamageType.scaling().equals(DamageScaling.NEVER))
                holder = Holder.direct(new DamageType(oldDamageType.msgId(), DamageScaling.NEVER, oldDamageType.exhaustion(),
                        oldDamageType.effects(), oldDamageType.deathMessageType()));
        }
        return new DamageSource(holder, attacker);
    }

    private static ResourceKey<DamageType> toDamageTypeResourceKey(@Nullable DamageCause cause, @Nullable Entity attacker) {
        /*
        net.minecraft.resources.ResourceKey IN_FIRE -> a
        net.minecraft.resources.ResourceKey LIGHTNING_BOLT -> b
        net.minecraft.resources.ResourceKey ON_FIRE -> c
        net.minecraft.resources.ResourceKey LAVA -> d
        net.minecraft.resources.ResourceKey HOT_FLOOR -> e
        net.minecraft.resources.ResourceKey IN_WALL -> f
        net.minecraft.resources.ResourceKey CRAMMING -> g
        net.minecraft.resources.ResourceKey DROWN -> h
        net.minecraft.resources.ResourceKey STARVE -> i
        net.minecraft.resources.ResourceKey CACTUS -> j
        net.minecraft.resources.ResourceKey FALL -> k
        net.minecraft.resources.ResourceKey FLY_INTO_WALL -> l
        net.minecraft.resources.ResourceKey FELL_OUT_OF_WORLD -> m
        net.minecraft.resources.ResourceKey GENERIC -> n
        net.minecraft.resources.ResourceKey MAGIC -> o
        net.minecraft.resources.ResourceKey WITHER -> p
        net.minecraft.resources.ResourceKey DRAGON_BREATH -> q
        net.minecraft.resources.ResourceKey DRY_OUT -> r
        net.minecraft.resources.ResourceKey SWEET_BERRY_BUSH -> s
        net.minecraft.resources.ResourceKey FREEZE -> t
        net.minecraft.resources.ResourceKey STALAGMITE -> u
        net.minecraft.resources.ResourceKey FALLING_BLOCK -> v
        net.minecraft.resources.ResourceKey FALLING_ANVIL -> w
        net.minecraft.resources.ResourceKey FALLING_STALACTITE -> x
        net.minecraft.resources.ResourceKey STING -> y
        net.minecraft.resources.ResourceKey MOB_ATTACK -> z
        net.minecraft.resources.ResourceKey MOB_ATTACK_NO_AGGRO -> A
        net.minecraft.resources.ResourceKey PLAYER_ATTACK -> B
        net.minecraft.resources.ResourceKey ARROW -> C
        net.minecraft.resources.ResourceKey TRIDENT -> D
        net.minecraft.resources.ResourceKey MOB_PROJECTILE -> E
        net.minecraft.resources.ResourceKey FIREWORKS -> F
        net.minecraft.resources.ResourceKey FIREBALL -> G
        net.minecraft.resources.ResourceKey UNATTRIBUTED_FIREBALL -> H
        net.minecraft.resources.ResourceKey WITHER_SKULL -> I
        net.minecraft.resources.ResourceKey THROWN -> J
        net.minecraft.resources.ResourceKey INDIRECT_MAGIC -> K
        net.minecraft.resources.ResourceKey THORNS -> L
        net.minecraft.resources.ResourceKey EXPLOSION -> M
        net.minecraft.resources.ResourceKey PLAYER_EXPLOSION -> N
        net.minecraft.resources.ResourceKey SONIC_BOOM -> O
        net.minecraft.resources.ResourceKey BAD_RESPAWN_POINT -> P
        net.minecraft.resources.ResourceKey OUTSIDE_BORDER -> Q
        net.minecraft.resources.ResourceKey GENERIC_KILL -> R
         */
        if (cause != null) {
            boolean attackerIsPlayer = attacker instanceof Player;
            return switch (cause) {
                case BLOCK_EXPLOSION -> DamageTypes.EXPLOSION;
                case CONTACT -> DamageTypes.CACTUS;
                case CRAMMING -> DamageTypes.CRAMMING;
                case CUSTOM, SUICIDE -> DamageTypes.GENERIC;
                case DRAGON_BREATH -> DamageTypes.DRAGON_BREATH;
                case DROWNING -> DamageTypes.DROWN;
                case DRYOUT, MELTING -> DamageTypes.DRY_OUT;
                case ENTITY_ATTACK -> attackerIsPlayer ? DamageTypes.PLAYER_ATTACK : DamageTypes.MOB_ATTACK;
                case ENTITY_EXPLOSION -> attackerIsPlayer ? DamageTypes.PLAYER_EXPLOSION : DamageTypes.EXPLOSION;
                case ENTITY_SWEEP_ATTACK ->
                        attackerIsPlayer ? DamageTypes.PLAYER_ATTACK : DamageTypes.MOB_ATTACK_NO_AGGRO;
                case FALL -> DamageTypes.FALL;
                case FALLING_BLOCK -> DamageTypes.FALLING_BLOCK;
                case FIRE -> DamageTypes.ON_FIRE;
                case FIRE_TICK -> DamageTypes.IN_FIRE;
                case FLY_INTO_WALL -> DamageTypes.FLY_INTO_WALL;
                case FREEZE -> DamageTypes.FREEZE;
                case HOT_FLOOR -> DamageTypes.HOT_FLOOR;
                case KILL -> DamageTypes.GENERIC_KILL;
                case LAVA -> DamageTypes.LAVA;
                case LIGHTNING -> DamageTypes.LIGHTNING_BOLT;
                case MAGIC -> DamageTypes.MAGIC;
                case POISON -> DamageTypes.INDIRECT_MAGIC;
                case PROJECTILE -> getProjectileDamageType(attacker);
                case SONIC_BOOM -> DamageTypes.SONIC_BOOM;
                case STARVATION -> DamageTypes.STARVE;
                case SUFFOCATION -> DamageTypes.IN_WALL;
                case THORNS -> DamageTypes.THORNS;
                case VOID -> DamageTypes.FELL_OUT_OF_WORLD;
                case WITHER -> DamageTypes.WITHER;
                case WORLD_BORDER -> DamageTypes.OUTSIDE_BORDER;
                default -> DamageTypes.GENERIC;
            };
        }
        return DamageTypes.GENERIC;
    }

    private static ResourceKey<DamageType> getProjectileDamageType(Entity attacker) {
        if (attacker instanceof Arrow)
            return DamageTypes.ARROW;
        else if (attacker instanceof Fireball)
            return DamageTypes.FIREBALL;
        else if (attacker instanceof FireworkRocketEntity)
            return DamageTypes.FIREWORKS;
        else if (attacker instanceof Trident)
            return DamageTypes.TRIDENT;
        else if (attacker instanceof WitherSkull)
            return DamageTypes.WITHER_SKULL;
        return DamageTypes.MOB_PROJECTILE;
    }
}
