package org.ricetea.barleyteaapi.internal.listener;

import java.util.HashMap;
import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.ricetea.barleyteaapi.api.block.BaseBlock;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockBreak;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockExplode;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockMove;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockPlace;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockTick;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockLoad;
import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockBreakByBlockExplode;
import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockBreakByPlayer;
import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockDropByPlayer;
import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockExplode;
import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockMove;
import org.ricetea.barleyteaapi.api.block.feature.data.DataBlockPlaceByPlayer;
import org.ricetea.barleyteaapi.api.block.registration.BlockRegister;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldPlayerPlace;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldPlayerPlaceBlock;
import org.ricetea.barleyteaapi.internal.chunk.ChunkStorage;
import org.ricetea.barleyteaapi.internal.helper.BlockFeatureHelper;
import org.ricetea.barleyteaapi.internal.helper.ItemFeatureHelper;
import org.ricetea.barleyteaapi.internal.task.BlockTickTask;
import org.ricetea.utils.Lazy;

public final class BlockListener implements Listener {
    private static final Lazy<BlockListener> inst = Lazy.create(BlockListener::new);

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
        try {
            if (!ItemFeatureHelper.doFeatureCancellable(event.getItemInHand(), event,
                    FeatureItemHoldPlayerPlace.class, FeatureItemHoldPlayerPlace::handleItemHoldPlayerPlaceBlock,
                    DataItemHoldPlayerPlaceBlock::new)) {
                event.setCancelled(true);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!BlockFeatureHelper.doFeatureCancellable(event.getBlock(), event, FeatureBlockPlace.class,
                    FeatureBlockPlace::handleBlockPlaceByPlayer, DataBlockPlaceByPlayer::new)) {
                event.setCancelled(true);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            BlockFeatureHelper.doFeature(event.getBlock(), FeatureBlockLoad.class,
                    FeatureBlockLoad::handleBlockLoaded);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenBlockBreak(BlockBreakEvent event) {
        if (event == null || event.isCancelled())
            return;
        Block block = event.getBlock();
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        if (block != null && register != null) {
            NamespacedKey id = BaseBlock.getBlockID(block);
            if (id != null) {
                BaseBlock baseBlock = register.lookup(id);
                if (baseBlock != null) {
                    if (baseBlock instanceof FeatureBlockBreak blockBreakFeature) {
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
                    if (baseBlock instanceof FeatureBlockTick) {
                        BlockTickTask.getInstance().removeBlock(block);
                    }
                    if (baseBlock instanceof FeatureBlockLoad feature) {
                        try {
                            feature.handleBlockUnloaded(block);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ChunkStorage.removeBlockDataContainer(block);
                    if (event.isDropItems()) {
                        PrepareToDrops.put(event.getBlock().getLocation(), id);
                    }
                }
            }
        }
    }

    private final HashMap<Location, NamespacedKey> PrepareToDrops = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenBlockDrop(BlockDropItemEvent event) {
        if (event == null || event.isCancelled())
            return;
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        if (register != null) {
            Block block = event.getBlock();
            NamespacedKey id = PrepareToDrops.remove(block.getLocation());
            if (id != null && register.lookup(id) instanceof FeatureBlockBreak blockBreakFeature) {
                blockBreakFeature.handleBlockDropByPlayer(new DataBlockDropByPlayer(event));
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenBlockExplode(BlockExplodeEvent event) {
        if (event == null || event.isCancelled())
            return;
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        if (register != null) {
            if (!BlockFeatureHelper.doFeatureCancellable(event.getBlock(), event, FeatureBlockExplode.class,
                    FeatureBlockExplode::handleBlockExplode, DataBlockExplode::new)) {
                event.setCancelled(true);
                return;
            }
            for (var iterator = event.blockList().iterator(); iterator.hasNext();) {
                Block block = iterator.next();
                if (block != null) {
                    NamespacedKey id = BaseBlock.getBlockID(block);
                    if (id != null) {
                        BaseBlock baseBlock = register.lookup(id);
                        if (baseBlock != null) {
                            if (baseBlock instanceof FeatureBlockBreak blockBreakFeature) {
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
                            if (baseBlock instanceof FeatureBlockTick) {
                                BlockTickTask.getInstance().removeBlock(block);
                            }
                            if (baseBlock instanceof FeatureBlockLoad feature) {
                                try {
                                    feature.handleBlockUnloaded(block);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            ChunkStorage.removeBlockDataContainer(block);
                            PrepareToDrops.put(block.getLocation(), id);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenBlockFromTo(BlockFromToEvent event) {
        if (event == null || event.isCancelled())
            return;
        Block from = event.getBlock();
        Block to = event.getToBlock();
        NamespacedKey id = BaseBlock.getBlockID(from);
        if (id != null && from != null && to != null) {
            BlockRegister register = BlockRegister.getInstanceUnsafe();
            if (register != null) {
                BaseBlock baseBlock = register.lookup(id);
                if (baseBlock instanceof FeatureBlockMove blockMoveFeature
                        && !blockMoveFeature.handleBlockMove(new DataBlockMove(event)) || event.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }
                PersistentDataContainer container = ChunkStorage.getBlockDataContainer(from, false);
                if (container != null) {
                    ChunkStorage.removeBlockDataContainer(from);
                }
                ChunkStorage.setBlockDataContainer(to, container);
                if (baseBlock instanceof FeatureBlockLoad feature) {
                    feature.handleBlockUnloaded(from);
                    feature.handleBlockLoaded(to);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenPistonExtend(BlockPistonExtendEvent event) {
        if (event == null || event.isCancelled())
            return;
        //Custom Block can't push or pull now...
        for (Block block : event.getBlocks()) {
            if (BaseBlock.isBarleyTeaBlock(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenPistonRetract(BlockPistonRetractEvent event) {
        if (event == null || event.isCancelled())
            return;
        //Custom Block can't push or pull now...
        for (Block block : event.getBlocks()) {
            if (BaseBlock.isBarleyTeaBlock(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
