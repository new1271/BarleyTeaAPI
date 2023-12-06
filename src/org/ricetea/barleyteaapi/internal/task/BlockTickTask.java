package org.ricetea.barleyteaapi.internal.task;

import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitScheduler;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.block.BaseBlock;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockTick;
import org.ricetea.barleyteaapi.api.block.registration.BlockRegister;
import org.ricetea.barleyteaapi.api.task.AbstractTask;
import org.ricetea.utils.Lazy;

public final class BlockTickTask extends AbstractTask {

    private static record BlockLocation(int x, int y, int z) {

        public BlockLocation(@Nonnull Block block) {
            this(block.getX(), block.getY(), block.getZ());
        }

        @Nonnull
        public Block getBlock(@Nonnull World world) {
            return world.getBlockAt(x, y, z);
        }

    }

    @Nonnull
    private static final Lazy<BlockTickTask> _inst = Lazy.create(BlockTickTask::new);

    @Nonnull
    private final Hashtable<World, Hashtable<BlockLocation, Integer>> tickingTable = new Hashtable<>();

    private int lastTick;

    /*
     * Operations
     * 0 = Add
     * 1 = Remove
     */

    @Nonnull
    private final ConcurrentHashMap<World, ConcurrentHashMap<BlockLocation, Integer>> operationTable = new ConcurrentHashMap<>();

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
            for (var iterator = operationTable.entrySet().iterator(); iterator.hasNext();) {
                var entry = iterator.next();
                World world = entry.getKey();
                var table = entry.getValue();
                var affectTable = tickingTable.computeIfAbsent(world, ignored -> new Hashtable<>());
                synchronized (table) {
                    for (var iterator2 = table.entrySet().iterator(); iterator2.hasNext(); iterator2.remove()) {
                        var entry2 = iterator2.next();
                        BlockLocation location = entry2.getKey();
                        Integer op = entry2.getValue();
                        if (op == null)
                            continue;
                        switch (op) {
                            case 0 -> {
                                affectTable.putIfAbsent(location, 0);
                            }
                            case 1 -> {
                                Integer id = affectTable.remove(location);
                                if (id != null && id != 0)
                                    scheduler.cancelTask(id);
                            }
                        }
                    }
                }
            }
            if (tickingTable.isEmpty() || tickingTable.values().stream().allMatch(Hashtable::isEmpty)) {
                stop();
            } else {
                int currentTick = Bukkit.getCurrentTick();
                if (currentTick != lastTick) {
                    lastTick = currentTick;
                    for (var entry : tickingTable.entrySet()) {
                        World world = entry.getKey();
                        var table = entry.getValue();
                        table.replaceAll((location, taskId) -> {
                            if (taskId != 0 && (scheduler.isCurrentlyRunning(taskId) || scheduler.isQueued(taskId)))
                                return taskId;
                            return scheduler.scheduleSyncDelayedTask(api, new _Task(world, location, table));
                        });
                    }
                }
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        tickingTable.clear();
        operationTable.clear();
    }

    public void addBlock(@Nullable Block block) {
        if (block == null || !BaseBlock.isBarleyTeaBlock(block) || !BarleyTeaAPI.checkPluginUsable())
            return;
        var table = operationTable.computeIfAbsent(block.getWorld(), ignored -> new ConcurrentHashMap<>());
        table.merge(new BlockLocation(block), 0, Math::max);
        if (!isRunning)
            start();
    }

    public void removeBlock(@Nullable Block block) {
        if (block == null || !BarleyTeaAPI.checkPluginUsable())
            return;
        var table = operationTable.computeIfAbsent(block.getWorld(), ignored -> new ConcurrentHashMap<>());
        table.merge(new BlockLocation(block), 1, Math::max);
        if (!isRunning)
            start();
    }

    private static class _Task implements Runnable {
        @Nonnull
        private final World world;
        @Nonnull
        private final BlockLocation location;
        @Nonnull
        private final Hashtable<BlockLocation, Integer> operationTable;

        _Task(@Nonnull World world, BlockLocation location,
                @Nonnull Hashtable<BlockLocation, Integer> blockOperationTable) {
            this.world = world;
            this.location = location;
            this.operationTable = blockOperationTable;
        }

        @Override
        public void run() {
            if (!doJob())
                operationTable.merge(location, 1, Math::max);
        }

        private boolean doJob() {
            BlockRegister register = BlockRegister.getInstanceUnsafe();
            if (register == null)
                return false;
            Block block = location.getBlock(world);
            if (block == null || block.isEmpty())
                return false;
            NamespacedKey id = BaseBlock.getBlockID(block);
            if (id == null)
                return false;
            BaseBlock baseBlock = register.lookup(id);
            if (baseBlock instanceof FeatureBlockTick feature) {
                try {
                    feature.handleTick(block);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            return baseBlock == null;
        }
    }
}
