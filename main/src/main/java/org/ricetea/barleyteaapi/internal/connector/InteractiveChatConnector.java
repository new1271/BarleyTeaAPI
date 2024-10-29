package org.ricetea.barleyteaapi.internal.connector;

import com.google.common.collect.ImmutableList;
import com.loohp.interactivechat.api.InteractiveChatAPI;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.internal.connector.patch.ApplyTranslateFallbacksFunction;
import org.ricetea.barleyteaapi.internal.connector.patch.InteractiveChatConnectorPatch;
import org.ricetea.barleyteaapi.util.connector.SoftDependConnector;
import org.ricetea.utils.Cache;
import org.ricetea.utils.ObjectUtil;
import org.ricetea.utils.WithFlag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ApiStatus.Internal
public final class InteractiveChatConnector implements SoftDependConnector {
    private final List<InteractiveChatConnectorPatch> patchList = new ArrayList<>();
    private final ReadWriteLock patchListLock = new ReentrantReadWriteLock();
    private final Cache<Collection<InteractiveChatConnectorPatch>> patchesCache =
            Cache.createThreadSafe(this::getPatchesReal);
    private final ApplyTranslateFallbacksFunction applyTranslateFallbacksFunction =
            ConnectorInternals.applyTranslateFallbacks(this::patchTranslateFallbacks);

    @Nullable
    private BarleyTeaAPI _apiInst;

    @Override
    public void onEnable(@Nonnull Plugin plugin) {
        BarleyTeaAPI apiInst = BarleyTeaAPI.getInstanceUnsafe();
        if (apiInst == null) {
            _apiInst = null;
            return;
        }
        _apiInst = apiInst;
        InteractiveChatAPI.registerItemStackTransformProvider(apiInst, 1, this::transformItemStack);
    }

    @Override
    public void onDisable() {
        BarleyTeaAPI apiInst = _apiInst;
        if (apiInst == null)
            return;
        InteractiveChatAPI.unregisterItemStackTransformProvider(apiInst);
    }

    public void addPatch(@Nonnull InteractiveChatConnectorPatch patch) {
        Lock lock = patchListLock.writeLock();
        lock.lock();
        patchList.add(patch);
        patchesCache.reset();
        lock.unlock();
    }

    public void removePatch(@Nonnull InteractiveChatConnectorPatch patch) {
        Lock lock = patchListLock.writeLock();
        lock.lock();
        patchList.remove(patch);
        patchesCache.reset();
        lock.unlock();
    }

    @Nonnull
    public Collection<InteractiveChatConnectorPatch> getPatches() {
        return patchesCache.get();
    }

    @Nonnull
    private Collection<InteractiveChatConnectorPatch> getPatchesReal() {
        Lock lock = patchListLock.readLock();
        lock.lock();
        Collection<InteractiveChatConnectorPatch> result = ImmutableList.copyOf(patchList);
        lock.unlock();
        return result;
    }

    @Nonnull
    private ItemStack transformItemStack(@Nullable ItemStack itemStack, @Nullable UUID uuid) {
        if (itemStack == null)
            return ItemStack.empty();
        return ObjectUtil.letNonNull(transformItemStackUnsafe(itemStack, uuid), ItemStack::empty);
    }

    @Nullable
    private ItemStack transformItemStackUnsafe(@Nonnull ItemStack itemStack, @Nullable UUID uuid) {
        Player player = ObjectUtil.safeMap(uuid, Bukkit::getPlayer);
        ItemStack result = ObjectUtil.safeMap(ItemHelper.renderUnsafe(itemStack, player), WithFlag::obj);
        if (result == null)
            return null;
        Locale locale = ObjectUtil.letNonNull(ObjectUtil.safeMap(player, Player::locale), Locale::getDefault);
        result = ObjectUtil.safeMap(
                ConnectorInternals.applyTranslateFallbacks(GlobalTranslator.translator(), result,
                        locale, this::patchTranslateFallbacks),
                WithFlag::obj);
        return result;
    }

    private boolean patchTranslateFallbacks(@Nonnull Translator translator, @Nonnull ItemMeta itemMeta,
                                            @Nonnull Locale locale) {
        boolean result = false;
        for (InteractiveChatConnectorPatch patch : getPatches()) {
            result |= patch.afterApplyTranslateFallbacks(translator, itemMeta, locale, applyTranslateFallbacksFunction);
        }
        return result;
    }
}
