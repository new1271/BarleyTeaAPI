package org.ricetea.barleyteaapi.internal.task;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.block.CustomBlock;
import org.ricetea.barleyteaapi.api.block.feature.FeatureBlockTick;
import org.ricetea.barleyteaapi.api.block.helper.BlockHelper;
import org.ricetea.barleyteaapi.api.block.registration.BlockRegister;
import org.ricetea.barleyteaapi.util.EnumUtil;
import org.ricetea.utils.CollectionUtil;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@ApiStatus.Internal
public final class BlockTickTask extends LoopTaskBase {

    @Nonnull
    private static final Lazy<BlockTickTask> _inst = Lazy.create(BlockTickTask::new);
    @Nonnull
    private final Map<NamespacedKey, Map<BlockLocation, Optional<BukkitTask>>> tickingTable = new HashMap<>();
    @Nonnull
    private final Map<NamespacedKey, Map<BlockLocation, Operation>> operationTable = new ConcurrentHashMap<>();
    private int lastTick;

    private BlockTickTask() {
        super(50);
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
    public void runLoop() {
        BarleyTeaAPI api = BarleyTeaAPI.getInstanceUnsafe();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        if (api == null || register == null || register.isEmpty()) {
            stop();
            return;
        }
        operationTable.forEach((worldKey, map) -> {
            Map<BlockLocation, Optional<BukkitTask>> tickingMap = tickingTable.computeIfAbsent(worldKey, (ignored) -> new HashMap<>());
            synchronized (map) {
                CollectionUtil.forEachAndRemoveAll(map.entrySet(), (location, operation) -> {
                    if (operation == null)
                        return;
                    switch (operation) {
                        case ADD -> tickingMap.putIfAbsent(location, Optional.empty());
                        case REMOVE -> {
                            Optional<BukkitTask> taskOptional = tickingMap.remove(location);
                            if (taskOptional == null)
                                return;
                            taskOptional.ifPresent((task) -> {
                                if (scheduler.isQueued(task.getTaskId())) {
                                    task.cancel();
                                }
                            });
                        }
                    }
                });
            }
        });
        if (tickingTable.isEmpty() || tickingTable.values().stream().allMatch(Map::isEmpty)) {
            for (Map<BlockLocation, Operation> map : operationTable.values()) {
                synchronized (map) {
                    if (!map.isEmpty())
                        return;
                }
            }
            stop();
            return;
        }
        int currentTick = Bukkit.getCurrentTick();
        if (currentTick == lastTick)
            return;
        lastTick = currentTick;
        tickingTable.forEach((worldKey, map) ->
                map.replaceAll((location, taskOptional) -> {
                    if (taskOptional.isPresent()) {
                        BukkitTask task = taskOptional.get();
                        if (scheduler.isQueued(task.getTaskId())) {
                            return taskOptional;
                        }
                    }
                    return Optional.of(scheduler.runTask(api, new _Task(worldKey, location)));
                }));
    }

    @Override
    public void stop() {
        super.stop();
        tickingTable.clear();
        CollectionUtil.forEachAndRemoveAll(operationTable.values(), (map) -> {
            synchronized (map) {
                map.clear();
            }
        });
    }

    public void addBlock(@Nullable Block block) {
        if (!BlockHelper.isCustomBlock(block) || !BarleyTeaAPI.checkPluginUsable())
            return;
        addBlock(block.getWorld().getKey(), new BlockLocation(block));
    }

    private void addBlock(@Nullable NamespacedKey worldKey, @Nullable BlockLocation location) {
        if (worldKey == null || location == null || !BarleyTeaAPI.checkPluginUsable())
            return;
        queueOperation(worldKey, location, Operation.ADD);
    }

    public void removeBlock(@Nullable Block block) {
        if (block == null)
            return;
        removeBlock(block.getWorld().getKey(), new BlockLocation(block));
    }

    private void removeBlock(@Nullable NamespacedKey worldKey, @Nullable BlockLocation location) {
        if (worldKey == null || location == null || !BarleyTeaAPI.checkPluginUsable())
            return;
        queueOperation(worldKey, location, Operation.REMOVE);
    }

    private void queueOperation(@Nonnull NamespacedKey worldKey, @Nonnull BlockLocation location, Operation operation) {
        Map<BlockLocation, Operation> map = operationTable.computeIfAbsent(worldKey, ignored -> new HashMap<>());
        synchronized (map) {
            boolean needStart;
            if (map.isEmpty()) {
                needStart = true;
                map.put(location, operation);
            } else {
                needStart = !isStarted();
                map.merge(location, operation, EnumUtil::maxOrdinal);
            }
            if (needStart)
                start();
        }
    }

    private enum Operation {
        ADD,
        REMOVE
    }

    private record BlockLocation(int x, int y, int z) {

        public BlockLocation(@Nonnull Block block) {
            this(block.getX(), block.getY(), block.getZ());
        }

        @Nonnull
        public Block getBlock(@Nonnull World world) {
            return world.getBlockAt(x, y, z);
        }

    }

    private record _Task(@Nonnull NamespacedKey worldKey, @Nonnull BlockLocation location) implements Runnable {

        @Override
        public void run() {
            if (doJob())
                return;
            BlockTickTask task = BlockTickTask.getInstanceUnsafe();
            if (task == null)
                return;
            task.removeBlock(worldKey, location);
        }

        private boolean doJob() {
            if (!BlockRegister.hasRegistered())
                return false;
            World world = Bukkit.getWorld(worldKey);
            if (world == null)
                return false;
            Block block = location.getBlock(world);
            if (block.isEmpty())
                return false;
            CustomBlock blockType = CustomBlock.get(block);
            if (blockType == null)
                return true;
            FeatureBlockTick feature = blockType.getFeature(FeatureBlockTick.class);
            if (feature != null) {
                ObjectUtil.tryCall(block, feature::handleTick);
                return true;
            }
            return false;
        }
    }
}
