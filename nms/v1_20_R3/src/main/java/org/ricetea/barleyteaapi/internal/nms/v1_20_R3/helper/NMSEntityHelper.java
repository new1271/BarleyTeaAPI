package org.ricetea.barleyteaapi.internal.nms.v1_20_R3.helper;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.ricetea.utils.ObjectUtil;

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
                nmsDamager, nmsDamagee instanceof Player && withoutScaling);
        if (cause != null) {
            switch (cause) {
                case ENTITY_SWEEP_ATTACK -> damageSource.sweep();
                case POISON -> damageSource.poison();
                case MELTING -> damageSource.melting();
            }
        }
        return nmsDamagee.hurt(damageSource, damage);
    }

    @Nonnull
    private static DamageSource buildDamageSource(@Nonnull HolderLookup.RegistryLookup<DamageType> registry,
                                                  @Nonnull ResourceKey<DamageType> key,
                                                  @Nullable Entity attacker,
                                                  boolean withoutScaling) {
        return buildDamageSource(registry.getOrThrow(key), attacker, withoutScaling);
    }

    @Nonnull
    private static DamageSource buildDamageSource(@Nonnull Holder<DamageType> holder, @Nullable Entity attacker,
                                                  boolean withoutScaling) {
        if (withoutScaling) {
            DamageType oldDamageType = holder.value();
            DamageScaling scaling = oldDamageType.scaling();
            if (scaling.equals(DamageScaling.ALWAYS) ||
                    (scaling.equals(DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER) && !(attacker instanceof Player))) {
                holder = Holder.direct(new DamageType(oldDamageType.msgId(),
                        DamageScaling.NEVER, oldDamageType.exhaustion(),
                        oldDamageType.effects(), oldDamageType.deathMessageType()));
            }
        }
        if (attacker instanceof Projectile projectile) {
            return new DamageSource(holder, attacker, projectile.getOwner());
        }
        return new DamageSource(holder, attacker);
    }

    @Nonnull
    private static ResourceKey<DamageType> toDamageTypeResourceKey(@Nullable DamageCause cause, @Nullable Entity attacker) {
        if (cause == null) {
            return DamageTypes.GENERIC;
        }
        boolean attackerIsPlayer = attacker instanceof Player;
        return switch (cause) {
            case BLOCK_EXPLOSION, ENTITY_EXPLOSION ->
                    attackerIsPlayer ? DamageTypes.PLAYER_EXPLOSION : DamageTypes.EXPLOSION;
            case CONTACT -> DamageTypes.CACTUS;
            case CRAMMING -> DamageTypes.CRAMMING;
            case DRAGON_BREATH -> DamageTypes.DRAGON_BREATH;
            case DROWNING, MELTING -> DamageTypes.DROWN;
            case DRYOUT -> DamageTypes.DRY_OUT;
            case ENTITY_ATTACK -> attackerIsPlayer ? DamageTypes.PLAYER_ATTACK : DamageTypes.MOB_ATTACK;
            case ENTITY_SWEEP_ATTACK -> attackerIsPlayer ? DamageTypes.PLAYER_ATTACK : DamageTypes.MOB_ATTACK_NO_AGGRO;
            case FALL -> DamageTypes.FALL;
            case FALLING_BLOCK -> {
                Material material;
                if (attacker instanceof FallingBlockEntity fallingBlock) {
                    try {
                        material = fallingBlock.getBlockState().getBukkitMaterial();
                    } catch (Exception e) {
                        material = Material.AIR;
                    }
                } else {
                    material = Material.AIR;
                }
                yield switch (material) {
                    case ANVIL -> DamageTypes.FALLING_ANVIL;
                    case POINTED_DRIPSTONE -> DamageTypes.FALLING_STALACTITE;
                    default -> DamageTypes.FALLING_BLOCK;
                };
            }
            case FIRE -> DamageTypes.IN_FIRE;
            case FIRE_TICK -> DamageTypes.ON_FIRE;
            case FLY_INTO_WALL -> DamageTypes.FLY_INTO_WALL;
            case FREEZE -> DamageTypes.FREEZE;
            case HOT_FLOOR -> DamageTypes.HOT_FLOOR;
            case KILL -> DamageTypes.GENERIC_KILL;
            case LAVA -> DamageTypes.LAVA;
            case LIGHTNING -> DamageTypes.LIGHTNING_BOLT;
            case MAGIC, POISON -> DamageTypes.MAGIC;
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

    @Nonnull
    private static ResourceKey<DamageType> getProjectileDamageType(@Nullable Entity attacker) {
        if (attacker instanceof Arrow)
            return DamageTypes.ARROW;
        if (attacker instanceof FireworkRocketEntity)
            return DamageTypes.FIREWORKS;
        if (attacker instanceof ThrownTrident)
            return DamageTypes.TRIDENT;
        if (attacker instanceof ShulkerBullet)
            return DamageTypes.MOB_PROJECTILE;
        boolean hasOwner = ObjectUtil.safeMap(ObjectUtil.tryCast(attacker, Projectile.class), Projectile::getOwner) != null;
        if (attacker instanceof Fireball)
            return hasOwner ? DamageTypes.FIREBALL : DamageTypes.UNATTRIBUTED_FIREBALL;
        if (attacker instanceof WitherSkull)
            return hasOwner ? DamageTypes.WITHER_SKULL : DamageTypes.MAGIC;
        return DamageTypes.MOB_ATTACK_NO_AGGRO;
    }
}
