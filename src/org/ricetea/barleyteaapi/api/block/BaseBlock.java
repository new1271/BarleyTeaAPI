package org.ricetea.barleyteaapi.api.block;

import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Consumer;
import org.ricetea.barleyteaapi.api.block.data.DataBlockType;
import org.ricetea.barleyteaapi.api.block.registration.BlockRegister;
import org.ricetea.barleyteaapi.internal.chunk.ChunkStorage;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;

public abstract class BaseBlock implements Keyed {
    @Nonnull
    private static final NamespacedKey BlockTagNamespacedKey = NamespacedKeyUtils.BarleyTeaAPI("block_id");
    @Nonnull
    private final NamespacedKey key;
    @Nonnull
    private final Material blockTypeBasedOn;

    public BaseBlock(@Nonnull NamespacedKey key, @Nonnull Material blockTypeBasedOn) {
        this.key = key;
        this.blockTypeBasedOn = blockTypeBasedOn;
    }

    @Nonnull
    public final NamespacedKey getKey() {
        return key;
    }

    @Nonnull
    public final String getNameInTranslateKey() {
        return "block." + key.getNamespace() + "." + key.getKey() + ".name";
    }

    @Nonnull
    public String getDefaultName() {
        return getNameInTranslateKey();
    }

    @Nonnull
    public final Material getBlockTypeBasedOn() {
        return blockTypeBasedOn;
    }

    @Nullable
    protected static final PersistentDataContainer getPersistentDataContainer(@Nonnull Block block, boolean create) {
        return ChunkStorage.getBlockDataContainer(block, create);
    }

    public final void register(@Nullable Block block) {
        if (block != null)
            Objects.requireNonNull(getPersistentDataContainer(block, true)).set(BlockTagNamespacedKey,
                    PersistentDataType.STRING, key.toString());
    }

    public final void register(@Nullable Block block,
            @Nullable Consumer<Block> afterBlockRegistered) {
        if (block != null) {
            Objects.requireNonNull(getPersistentDataContainer(block, true)).set(BlockTagNamespacedKey,
                    PersistentDataType.STRING, key.toString());
            if (afterBlockRegistered != null) {
                afterBlockRegistered.accept(block);
            }
        }
    }

    public final boolean tryRegister(@Nullable Block block,
            @Nullable Predicate<Block> afterBlockRegistered) {
        if (block != null) {
            PersistentDataContainer container = Objects.requireNonNull(getPersistentDataContainer(block, true));
            String previousID = container.getOrDefault(BlockTagNamespacedKey, PersistentDataType.STRING, null);
            container.set(BlockTagNamespacedKey, PersistentDataType.STRING, key.toString());
            if (afterBlockRegistered != null) {
                if (!afterBlockRegistered.test(block)) {
                    if (!block.isEmpty())
                        if (previousID == null) {
                            container.remove(BlockTagNamespacedKey);
                            if (container.isEmpty()) {
                                ChunkStorage.removeBlockDataContainer(block);
                            }
                        } else
                            container.set(BlockTagNamespacedKey, PersistentDataType.STRING, previousID);
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public final boolean isCertainBlock(@Nullable Block block) {
        if (block != null) {
            PersistentDataContainer container = getPersistentDataContainer(block, false);
            if (container != null) {
                return key.toString()
                        .equals(container.getOrDefault(BlockTagNamespacedKey, PersistentDataType.STRING, null));
            }
        }
        return false;
    }

    public static void registerBlock(@Nullable Block block, @Nonnull BaseBlock blockType) {
        blockType.register(block);
    }

    public static boolean isBarleyTeaBlock(@Nullable Block block) {
        if (block != null) {
            PersistentDataContainer container = getPersistentDataContainer(block, false);
            if (container != null) {
                return container.has(BlockTagNamespacedKey, PersistentDataType.STRING);
            }
        }
        return false;
    }

    @Nullable
    public static NamespacedKey getBlockID(@Nullable Block block) {
        if (block == null)
            return null;
        PersistentDataContainer container = getPersistentDataContainer(block, false);
        if (container == null)
            return null;
        String namespacedKeyString = container.getOrDefault(BlockTagNamespacedKey, PersistentDataType.STRING, null);
        return namespacedKeyString == null ? null
                : namespacedKeyString.contains(":") ? NamespacedKey.fromString(namespacedKeyString) : null;
    }

    public static boolean isCertainBlock(@Nullable Block block, @Nonnull BaseBlock blockType) {
        return blockType.isCertainBlock(block);
    }

    @Nonnull
    public static DataBlockType getBlockType(@Nonnull Block block) {
        NamespacedKey blockTypeID = BaseBlock.getBlockID(block);
        if (blockTypeID == null) {
            return DataBlockType.create(block.getType());
        } else {
            BlockRegister register = BlockRegister.getInstanceUnsafe();
            if (register == null) {
                return DataBlockType.create(block.getType());
            } else {
                BaseBlock baseBlock = register.lookup(blockTypeID);
                if (baseBlock == null)
                    return DataBlockType.create(block.getType());
                else
                    return DataBlockType.create(baseBlock);
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof BaseBlock baseBlock) {
            return key.equals(baseBlock.getKey());
        }
        return super.equals(obj);
    }
}
