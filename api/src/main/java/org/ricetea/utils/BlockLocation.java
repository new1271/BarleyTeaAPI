package org.ricetea.utils;

import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.Position;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.lang.ref.WeakReference;
import java.util.Objects;

@Immutable
public final class BlockLocation {
    @Nonnull
    private final WeakReference<World> worldRef;
    private final int x, y, z;

    public BlockLocation(@Nonnull Block block) {
        this(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    public BlockLocation(@Nonnull Location location) {
        this(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public BlockLocation(@Nonnull World world, int x, int y, int z) {
        worldRef = new WeakReference<>(world);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof BlockLocation another) {
            World world = getWorldUnsafe();
            if (world == null)
                return false;
            World worldAnother = another.getWorldUnsafe();
            return Objects.equals(world, worldAnother) &&
                    x == another.x && y == another.y && z == another.z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        //Use bukkit location hashcode code
        int hash = 3;
        World world = getWorldUnsafe();
        hash = 19 * hash + (world != null ? world.hashCode() : 0);
        hash = 19 * hash + (int)(Double.doubleToLongBits(this.x) ^ Double.doubleToLongBits(this.x) >>> 32);
        hash = 19 * hash + (int)(Double.doubleToLongBits(this.y) ^ Double.doubleToLongBits(this.y) >>> 32);
        hash = 19 * hash + (int)(Double.doubleToLongBits(this.z) ^ Double.doubleToLongBits(this.z) >>> 32);
        return hash;
    }

    @Nonnull
    public World getWorld() {
        return Objects.requireNonNull(worldRef.get());
    }

    @Nullable
    public World getWorldUnsafe() {
        return worldRef.get();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Nonnull
    public Block getBlock() {
        return getWorld().getBlockAt(x, y, z);
    }

    @Nullable
    public Block getBlockUnsafe() {
        World world = getWorldUnsafe();
        if (world == null)
            return null;
        return world.getBlockAt(x, y, z);
    }

    @Nonnull
    public Location toLocation() {
        return new Location(getWorld(), x, y, z);
    }

    @Nullable
    public Location toLocationUnsafe() {
        World world = getWorldUnsafe();
        if (world == null)
            return null;
        return new Location(world, x, y, z);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Nonnull
    public BlockPosition toBlockPosition() {
        return Position.block(x, y, z);
    }
}
