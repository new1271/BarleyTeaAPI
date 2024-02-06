package org.ricetea.barleyteaapi.api.internal.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.item.CustomItemRarity;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
@ApiStatus.Internal
public abstract class CustomItemRarityBase implements CustomItemRarity {
    @Nonnull
    @Override
    public Component apply(@Nonnull Component component, boolean isRenamed, boolean force) {
        Component result;
        Style style = getStyle();
        if (force) {
            result = component.style(style)
                    .decoration(TextDecoration.ITALIC,
                            (style.hasDecoration(TextDecoration.ITALIC) ^ isRenamed) ? TextDecoration.State.TRUE : TextDecoration.State.FALSE);
        } else {
            result = component;
            if (component.color() == null) {
                result = result.color(style.color());
            }
            if (component.decorations().entrySet().stream().allMatch(entry -> entry.getValue().equals(TextDecoration.State.NOT_SET))) {
                result = result.decorations(style.decorations())
                        .decoration(TextDecoration.ITALIC,
                                (style.hasDecoration(TextDecoration.ITALIC) ^ isRenamed) ? TextDecoration.State.TRUE : TextDecoration.State.FALSE);
            }
        }
        return result;
    }
}
