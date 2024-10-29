package org.ricetea.barleyteaapi.internal.connector;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.translation.Translator;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.ricetea.barleyteaapi.api.internal.nms.INMSItemHelper;
import org.ricetea.barleyteaapi.internal.connector.patch.ApplyTranslateFallbacksFunction;
import org.ricetea.utils.Box;
import org.ricetea.utils.Converters;
import org.ricetea.utils.ObjectUtil;
import org.ricetea.utils.WithFlag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

final class ConnectorInternals {

    @FunctionalInterface
    public interface PatchTranslateFallbackFunction {
        boolean patchTranslateFallback(@Nonnull Translator translator, @Nonnull ItemMeta itemMeta,
                                       @Nonnull Locale locale);
    }

    @Nonnull
    public static ApplyTranslateFallbacksFunction applyTranslateFallbacks(@Nullable PatchTranslateFallbackFunction function) {
        return new ApplyTranslateFallbacksFunctionHandler(function);
    }

    @Nullable
    public static WithFlag<ItemStack> applyTranslateFallbacks(@Nonnull Translator translator,
                                                              @Nullable ItemStack itemStack,
                                                              @Nonnull Locale locale,
                                                              @Nullable PatchTranslateFallbackFunction function) {
        if (itemStack == null)
            return null;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return new WithFlag<>(itemStack, false);
        boolean isDirty = false;
        Component displayName = ObjectUtil.tryMapSilently(meta::displayName);
        if (displayName != null) {
            meta.displayName(applyTranslateFallbacks(translator, displayName, locale, function));
            isDirty = true;
        }
        List<Component> lore = ObjectUtil.tryMapSilently(meta::lore);
        if (lore != null) {
            meta.lore(lore.stream()
                    .map(loreLine -> applyTranslateFallbacks(translator, loreLine, locale, function))
                    .toList());
            isDirty = true;
        }
        if (meta instanceof BlockStateMeta blockMeta) {
            if (blockMeta.getBlockState() instanceof ShulkerBox shulkerBox) {
                var inventory = shulkerBox.getInventory();
                Box<Boolean> flag = Box.box(false);
                for (var iterator = inventory.iterator(); iterator.hasNext(); ) {
                    WithFlag<ItemStack> result = applyTranslateFallbacks(translator, iterator.next(), locale, function);
                    if (result != null && result.flag()) {
                        flag.set(true);
                        iterator.set(result.obj());
                    }
                }
                if (!isDirty)
                    isDirty = ObjectUtil.letNonNull(flag.get(), false);
                blockMeta.setBlockState(shulkerBox);
            }
        }
        if (function != null) {
            isDirty |= function.patchTranslateFallback(translator, meta, locale);
        }
        if (isDirty) {
            itemStack.setItemMeta(meta);
        }
        return new WithFlag<>(itemStack, isDirty);
    }

    @Nullable
    public static Component applyTranslateFallbacks(@Nonnull Translator translator,
                                                    @Nullable Component component,
                                                    @Nonnull Locale locale,
                                                    @Nullable PatchTranslateFallbackFunction function) {
        if (component == null)
            return null;
        Component result;
        if (component instanceof TranslatableComponent translatableComponent) {
            TranslatableComponent translatableResult = translatableComponent;
            if (translatableComponent.fallback() == null) {
                MessageFormat format = translator.translate(translatableComponent.key(), locale);
                if (format != null)
                    translatableResult = translatableResult.fallback(Converters.toStringFormat(format));
            }
            translatableResult = translatableResult
                    .args(translatableResult.args()
                            .stream()
                            .map(arg -> applyTranslateFallbacks(translator, arg, locale, function))
                            .toList());
            result = translatableResult;
        } else {
            result = component;
        }
        HoverEvent<?> hoverEvent = result.hoverEvent();
        if (hoverEvent != null) {
            result = processChatMessageHoverEvents(translator, result, locale, function);
        }
        return result
                .children(component.children()
                        .stream()
                        .map(arg -> applyTranslateFallbacks(translator, arg, locale, function))
                        .toList());
    }

    @Nonnull
    public static Component processChatMessageHoverEvents(@Nonnull Translator translator,
                                                          @Nonnull Component component,
                                                          @Nonnull Locale locale,
                                                          @Nullable PatchTranslateFallbackFunction function) {
        HoverEvent<?> hoverEvent = component.hoverEvent();
        if (hoverEvent != null && hoverEvent.value() instanceof HoverEvent.ShowItem showItem) {
            INMSItemHelper nmsItemHelper = INMSItemHelper.getInstanceUnsafe();
            if (nmsItemHelper == null)
                return component;
            ItemStack itemStack = nmsItemHelper.createItemStackFromShowItem(showItem);
            if (itemStack == null)
                return component;
            var flag = applyTranslateFallbacks(translator, itemStack, locale, function);
            if (flag != null && flag.flag()) {
                itemStack = flag.obj();
                return itemStack.displayName()
                        .hoverEvent(itemStack.asHoverEvent())
                        .children(component.children());
            }
        }
        return component;
    }

    private static final class ApplyTranslateFallbacksFunctionHandler implements ApplyTranslateFallbacksFunction {

        @Nullable
        private final PatchTranslateFallbackFunction _function;

        public ApplyTranslateFallbacksFunctionHandler(@Nullable PatchTranslateFallbackFunction function) {
            _function = function;
        }

        @Nullable
        @Override
        public Component apply(@Nonnull Translator translator, @Nullable Component component, @Nonnull Locale locale) {
            return ConnectorInternals.applyTranslateFallbacks(translator, component, locale, _function);
        }
    }
}
