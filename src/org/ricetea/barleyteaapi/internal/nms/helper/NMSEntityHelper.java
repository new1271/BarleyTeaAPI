package org.ricetea.barleyteaapi.internal.nms.helper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

public final class NMSEntityHelper {

    public static void dropItem(org.bukkit.entity.Player player, org.bukkit.inventory.ItemStack item) {
        //net.minecraft.world.entity.item.ItemEntity drop(net.minecraft.world.item.ItemStack,boolean,boolean) -> a
        ((CraftPlayer) player).getHandle().a(CraftItemStack.asNMSCopy(item), true, true);
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
        var world = nmsDamagee.dI();
        DamageSource damageSource = buildDamageSource(world.B_().d(Registries.p), toDamageTypeResourceKey(cause),
                nmsDamager, withoutScaling);
        return nmsDamagee.a(damageSource, damage);
    }

    private static DamageSource buildDamageSource(IRegistry<DamageType> registry, ResourceKey<DamageType> key,
            @Nullable net.minecraft.world.entity.Entity attacker, boolean withoutScaling) {
        Holder<DamageType> holder = registry.f(key);
        if (withoutScaling) {
            DamageType oldDamageType = holder.a();
            if (!oldDamageType.b().equals(DamageScaling.a))
                holder = Holder.a(new DamageType(oldDamageType.a(), DamageScaling.a, oldDamageType.c(),
                        oldDamageType.d(), oldDamageType.e()));
        }
        return new DamageSource(holder, attacker);
    }

    private static ResourceKey<DamageType> toDamageTypeResourceKey(@Nullable DamageCause cause) {
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
            switch (cause) {
                case BLOCK_EXPLOSION:
                    return DamageTypes.M;
                case CONTACT:
                    return DamageTypes.j;
                case CRAMMING:
                    return DamageTypes.g;
                case CUSTOM:
                    return DamageTypes.n;
                case DRAGON_BREATH:
                    return DamageTypes.q;
                case DROWNING:
                    return DamageTypes.h;
                case DRYOUT:
                    return DamageTypes.r;
                case ENTITY_ATTACK:
                    return DamageTypes.z;
                case ENTITY_EXPLOSION:
                    return DamageTypes.M;
                case ENTITY_SWEEP_ATTACK:
                    return DamageTypes.A;
                case FALL:
                    return DamageTypes.k;
                case FALLING_BLOCK:
                    return DamageTypes.v;
                case FIRE:
                    return DamageTypes.a;
                case FIRE_TICK:
                    return DamageTypes.c;
                case FLY_INTO_WALL:
                    return DamageTypes.l;
                case FREEZE:
                    return DamageTypes.t;
                case HOT_FLOOR:
                    return DamageTypes.e;
                case KILL:
                    return DamageTypes.R;
                case LAVA:
                    return DamageTypes.d;
                case LIGHTNING:
                    return DamageTypes.b;
                case MAGIC:
                    return DamageTypes.o;
                case MELTING:
                    return DamageTypes.e;
                case POISON:
                    return DamageTypes.K;
                case PROJECTILE:
                    return DamageTypes.E;
                case SONIC_BOOM:
                    return DamageTypes.O;
                case STARVATION:
                    return DamageTypes.i;
                case SUFFOCATION:
                    return DamageTypes.f;
                case SUICIDE:
                    return DamageTypes.n;
                case THORNS:
                    return DamageTypes.L;
                case VOID:
                    return DamageTypes.m;
                case WITHER:
                    return DamageTypes.p;
                case WORLD_BORDER:
                    return DamageTypes.Q;
                default:
                    break;

            }
        }
        return DamageTypes.n;
    }
}
