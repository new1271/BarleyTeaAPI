package org.ricetea.barleyteaapi.internal.task;

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
import org.ricetea.barleyteaapi.util.CachedList;
import org.ricetea.barleyteaapi.util.Lazy;

public final class BlockTickTask extends AbstractTask {

    @Nonnull
    private static final Lazy<BlockTickTask> _inst = new Lazy<>(BlockTickTask::new);

    @Nonnull
    private final CachedList<Block> blocks = new CachedList<>(Block.class);

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
        BarleyTeaAPI api = BarleyTeaAPI.getInstance();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        BlockRegister register = BlockRegister.getInstanceUnsafe();
        if (api == null || scheduler == null || register == null || !register.hasAnyRegistered()) {
            stop();
        } else {
            Block[] blocks;
            synchronized (this.blocks) {
                blocks = this.blocks.toArrayCasted();
            }
            if (blocks != null) {
                for (Block block : blocks) {
                    if (block == null || block.isEmpty()) {
                        removeBlock(block);
                    } else {
                        NamespacedKey id = BaseBlock.getBlockID(block);
                        if (id == null) {
                            removeBlock(block);
                        } else {
                            BaseBlock baseBlock = register.lookup(id);
                            if (baseBlock instanceof FeatureBlockTick tickingBlock)
                                try {
                                    scheduler.runTask(api, () -> tickingBlock.handleTick(block));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            else
                                removeBlock(block);
                        }
                    }
                }
            }
        }
    }

    public void addBlock(@Nullable Block block) {
        if (block == null || !BaseBlock.isBarleyTeaBlock(block) || !BarleyTeaAPI.checkPluginUsable())
            return;
        synchronized (blocks) {
            if (!blocks.contains(block))
                blocks.add(block);
            if (!isRunning) {
                start();
            }
        }
    }

    public void removeBlock(@Nullable Block block) {
        if (block == null || !BarleyTeaAPI.checkPluginUsable())
            return;
        synchronized (blocks) {
            blocks.remove(block);
            if (blocks.size() <= 0) {
                stop();
            }
        }
    }
}
