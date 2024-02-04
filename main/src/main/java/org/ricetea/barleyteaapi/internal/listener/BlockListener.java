package org.ricetea.barleyteaapi.internal.listener;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.block.CustomBlock;
import org.ricetea.barleyteaapi.api.block.CustomBlockType;
import org.ricetea.barleyteaapi.api.block.feature.*;
import org.ricetea.barleyteaapi.api.block.feature.data.*;
import org.ricetea.barleyteaapi.api.block.helper.BlockHelper;
import org.ricetea.barleyteaapi.api.block.registration.BlockRegister;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldPlayerPlace;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldPlayerPlaceBlock;
import org.ricetea.barleyteaapi.internal.chunk.ChunkStorage;
import org.ricetea.barleyteaapi.internal.linker.BlockFeatureLinker;
import org.ricetea.barleyteaapi.internal.linker.ItemFeatureLinker;
import org.ricetea.barleyteaapi.internal.task.BlockTickTask;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.util.HashMap;

@Singleton
@ApiStatus.Internal
public final class BlockListener implements Listener {
    private static final Lazy<BlockListener> inst = Lazy.create(BlockListener::new);
    private final HashMap<Location, NamespacedKey> PrepareToDrops = new HashMap<>();

    private BlockListener() {
    }

    @Nonnull
    public static BlockListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenBlockPlace(BlockPlaceEvent event) {
        if (event == null || event.isCancelled())
            return;
        Block block = event.getBlock();
        try {
            if (!ItemFeatureLinker.doFeatureCancellable(event.getItemInHand(), event,
                    FeatureItemHoldPlayerPlace.class, FeatureItemHoldPlayerPlace::handleItemHoldPlayerPlaceBlock,
                    DataItemHoldPlayerPlaceBlock::new)) {
                event.setCancelled(true);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!BlockFeatureLinker.doFeatureCancellable(block, event, FeatureBlockPlace.class,
                    FeatureBlockPlace::handleBlockPlaceByPlayer, DataBlockPlaceByPlayer::new)) {
                event.setCancelled(true);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        CustomBlock blockType = CustomBlock.get(block);
        BlockFeatureLinker.loadBlock(blockType, block);
        if (blockType instanceof FeatureBlockTick) {
            BlockTickTask.getInstance().addBlock(event.getBlock());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenBlockBreak(BlockBreakEvent event) {
        if (event == null || event.isCancelled())
            return;
        Block block = event.getBlock();
        CustomBlock blockType = CustomBlock.get(block);
        if (blockType == null)
            return;
        if (blockType instanceof FeatureBlockBreak blockBreakFeature) {
            try {
                if (!blockBreakFeature.handleBlockBreakByPlayer(new DataBlockBreakByPlayer(event))
                        || event.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        BlockFeatureLinker.unloadBlock(blockType, block);
        if (blockType instanceof FeatureBlockTick) {
            BlockTickTask.getInstance().removeBlock(block);
        }
        ChunkStorage.removeBlockDataContainer(block);
        if (event.isDropItems()) {
            PrepareToDrops.put(event.getBlock().getLocation(), blockType.getKey());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenBlockDrop(BlockDropItemEvent event) {
        if (event == null || event.isCancelled() || !BlockRegister.hasRegistered())
            return;
        Block block = event.getBlock();
        NamespacedKey id = PrepareToDrops.remove(block.getLocation());
        if (id != null && BlockRegister.getInstance().lookup(id) instanceof FeatureBlockBreak feature) {
            feature.handleBlockDropByPlayer(new DataBlockDropByPlayer(event));
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenBlockExplode(BlockExplodeEvent event) {
        if (event == null || event.isCancelled() || !BlockRegister.hasRegistered())
            return;
        if (!BlockFeatureLinker.doFeatureCancellable(event.getBlock(), event, FeatureBlockExplode.class,
                FeatureBlockExplode::handleBlockExplode, DataBlockExplode::new)) {
            event.setCancelled(true);
            return;
        }
        for (var iterator = event.blockList().iterator(); iterator.hasNext(); ) {
            Block block = iterator.next();
            if (block == null)
                continue;
            CustomBlock blockType = CustomBlock.get(block);
            if (blockType == null)
                continue;
            if (blockType instanceof FeatureBlockBreak blockBreakFeature) {
                try {
                    if (!blockBreakFeature
                            .handleBlockBreakByBlockExplode(
                                    new DataBlockBreakByBlockExplode(event, block))) {
                        iterator.remove();
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (event.isCancelled()) {
                    return;
                }
            }
            BlockFeatureLinker.unloadBlock(blockType, block);
            if (blockType instanceof FeatureBlockTick) {
                BlockTickTask.getInstance().removeBlock(block);
            }
            ChunkStorage.removeBlockDataContainer(block);
            PrepareToDrops.put(block.getLocation(), blockType.getKey());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenBlockFromTo(BlockFromToEvent event) {
        if (event == null || event.isCancelled())
            return;
        Block from = event.getBlock();
        CustomBlock blockType = CustomBlockType.get(from).asCustomBlock();
        if (blockType == null)
            return;
        if (blockType instanceof FeatureBlockMove blockMoveFeature
                && !blockMoveFeature.handleBlockMove(new DataBlockMove(event)) || event.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        PersistentDataContainer container = ChunkStorage.getBlockDataContainer(from, false);
        if (container != null) {
            ChunkStorage.removeBlockDataContainer(from);
        }
        Block to = event.getToBlock();
        ChunkStorage.setBlockDataContainer(to, container);
        if (blockType instanceof FeatureBlockLoad feature) {
            BlockFeatureLinker.unloadBlock(feature, from);
            BlockFeatureLinker.loadBlock(feature, to);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenPistonExtend(BlockPistonExtendEvent event) {
        if (event == null || event.isCancelled())
            return;
        //Custom Block can't push or pull now...
        if (event.getBlocks().stream().anyMatch(BlockHelper::isCustomBlock)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void listenPistonRetract(BlockPistonRetractEvent event) {
        if (event == null || event.isCancelled())
            return;
        //Custom Block can't push or pull now...
        if (event.getBlocks().stream().anyMatch(BlockHelper::isCustomBlock)) {
            event.setCancelled(true);
        }
    }
}
