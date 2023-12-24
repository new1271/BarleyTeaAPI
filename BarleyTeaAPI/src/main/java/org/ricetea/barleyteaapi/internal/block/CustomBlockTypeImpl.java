package org.ricetea.barleyteaapi.internal.block;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.block.CustomBlock;
import org.ricetea.barleyteaapi.api.block.CustomBlockType;
import org.ricetea.barleyteaapi.api.block.registration.BlockRegister;
import org.ricetea.utils.Either;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Immutable
@ApiStatus.Internal
public class CustomBlockTypeImpl extends Either<Material, CustomBlock> implements CustomBlockType {
    @Nonnull
    private static final ConcurrentHashMap<Material, CustomBlockType> vanillaMaterialMap = new ConcurrentHashMap<>();
    @Nonnull
    private static final ConcurrentHashMap<CustomBlock, CustomBlockType> customBlockMap = new ConcurrentHashMap<>();

    private CustomBlockTypeImpl(@Nullable Material left) {
        super(left, null);
    }

    private CustomBlockTypeImpl(@Nullable CustomBlock right) {
        super(null, right);
    }

    @Nullable
    @Override
    public Material asMaterial() {
        return left();
    }

    @Nullable
    @Override
    public CustomBlock asCustomBlock() {
        return right();
    }

    @Nonnull
    public static CustomBlockType get(@Nonnull Material material) {
        return vanillaMaterialMap.computeIfAbsent(material, CustomBlockTypeImpl::new);
    }

    @Nonnull
    public static CustomBlockType get(@Nonnull CustomBlock customBlock) {
        return customBlockMap.computeIfAbsent(customBlock, CustomBlockTypeImpl::new);
    }

    @Nonnull
    public static CustomBlockType get(@Nonnull NamespacedKey key) {
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        if (register != null) {
            CustomBlock blockType = register.lookup(key);
            if (blockType != null)
                return get(blockType);
        }
        Optional<Material> materialOptional = Arrays.stream(Material.values())
                .filter(material -> material.getKey().equals(key))
                .findAny();
        return materialOptional.map(CustomBlockType::get).orElseGet(CustomBlockType::empty);
    }

    @Nonnull
    public static CustomBlockType get(@Nonnull Block block) {
        CustomBlock blockType = CustomBlock.get(block);
        return blockType == null ? get(block.getType()) : get(blockType);
    }

    @ApiStatus.Internal
    public static void removeInstances(@Nonnull Collection<CustomBlock> blocks) {
        blocks.forEach(customBlockMap::remove);
    }
}
