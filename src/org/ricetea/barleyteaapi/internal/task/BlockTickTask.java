package org.ricetea.barleyteaapi.internal.task;

import java.util.Hashtable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitScheduler;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.block.BaseBlock;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockTick;
import org.ricetea.barleyteaapi.api.block.registration.BlockRegister;
import org.ricetea.barleyteaapi.api.task.AbstractTask;
import org.ricetea.utils.Lazy;

public final class BlockTickTask extends AbstractTask {

    @Nonnull
    private static final Lazy<BlockTickTask> _inst = Lazy.create(BlockTickTask::new);

    @Nonnull
    private final Hashtable<Block, Integer> tickingTable = new Hashtable<>();

    /*
     * Operations
     * 0 = Add
     * 1 = Reset Task Id
     * 2 = Remove
     */

    @Nonnull
    private final Hashtable<Block, Integer> operationTable = new Hashtable<>();

    private BlockTickTask() {
        super(50, 0);
    }

    @Nullable
    public static BlockTickTask getInstanceUnsafe() {
        return _inst.getUnsafe();
    }

    @Nonnull
    public static BlockTickTask getInstance() {
        return _inst.get();
    }

    @Override
    protected void runInternal() {
        BarleyTeaAPI api = BarleyTeaAPI.getInstanceUnsafe();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        if (api == null || scheduler == null || register == null || !register.hasAnyRegistered()) {
            stop();
        } else {
            synchronized (operationTable) {
                operationTable.forEach((block, op) -> {
                    switch (op) {
                        case 0 -> {
                            tickingTable.putIfAbsent(block, 0);
                        }
                        case 1 -> {
                            tickingTable.computeIfPresent(block, (a, b) -> 0);
                        }
                        case 2 -> {
                            tickingTable.remove(block);
                        }
                    }
                });
                operationTable.clear();
            }
            if (tickingTable.isEmpty()) {
                stop();
            } else {
                tickingTable.replaceAll((block, taskId) -> {
                    if (taskId != 0)
                        return taskId;
                    if (block == null || block.isEmpty()) {
                        removeBlock(block);
                        return 0;
                    }
                    NamespacedKey id = BaseBlock.getBlockID(block);
                    if (id == null) {
                        removeBlock(block);
                        return 0;
                    }
                    BaseBlock baseBlock = register.lookup(id);
                    if (baseBlock instanceof FeatureBlockTick tickingBlock) {
                        try {
                            return scheduler.scheduleSyncDelayedTask(api,
                                    new _Task(tickingBlock, block, operationTable));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        removeBlock(block);
                    }
                    return 0;
                });
            }
        }
    }

    public void addBlock(@Nullable Block block) {
        if (block == null || !BaseBlock.isBarleyTeaBlock(block) || !BarleyTeaAPI.checkPluginUsable())
            return;
        synchronized (operationTable) {
            operationTable.merge(block, 0, Math::max);
        }
        if (!isRunning)
            start();
    }

    public void removeBlock(@Nullable Block block) {
        if (block == null || !BarleyTeaAPI.checkPluginUsable())
            return;
        synchronized (operationTable) {
            operationTable.merge(block, 2, Math::max);
        }
        if (!isRunning)
            start();
    }

    private static class _Task implements Runnable {
        @Nonnull
        private final FeatureBlockTick feature;
        @Nonnull
        private final Block entity;
        @Nonnull
        private final Hashtable<Block, Integer> operationTable;

        _Task(@Nonnull FeatureBlockTick feature, @Nonnull Block entity,
                @Nonnull Hashtable<Block, Integer> entityOperationTable) {
            this.feature = feature;
            this.entity = entity;
            this.operationTable = entityOperationTable;
        }

        @Override
        public void run() {
            try {
                feature.handleTick(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            synchronized (operationTable) {
                operationTable.merge(entity, 1, Math::max);
            }
        }
    }
}
