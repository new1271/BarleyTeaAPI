package org.ricetea.barleyteaapi.api.helper;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import javax.annotation.Nullable;
import java.util.ArrayList;

public final class ChatColorHelper {
    @SuppressWarnings("deprecation")
    @Nullable
    public static Style toKyoriStyle(@Nullable net.md_5.bungee.api.ChatColor... chatColors) {
        if (chatColors == null)
            return null;
        TextColor color = null;
        ArrayList<TextDecoration> decorations = new ArrayList<>();
        for (net.md_5.bungee.api.ChatColor chatColor : chatColors) {
            if (chatColor == net.md_5.bungee.api.ChatColor.RESET) {
                color = null;
                decorations.clear();
            } else {
                java.awt.Color _color = chatColor.getColor();
                if (_color == null) {
                    if (chatColor.equals(net.md_5.bungee.api.ChatColor.ITALIC))
                        decorations.add(TextDecoration.ITALIC);
                    else if (chatColor.equals(net.md_5.bungee.api.ChatColor.BOLD))
                        decorations.add(TextDecoration.BOLD);
                    else if (chatColor.equals(net.md_5.bungee.api.ChatColor.STRIKETHROUGH))
                        decorations.add(TextDecoration.STRIKETHROUGH);
                    else if (chatColor.equals(net.md_5.bungee.api.ChatColor.UNDERLINE))
                        decorations.add(TextDecoration.UNDERLINED);
                    else if (chatColor.equals(net.md_5.bungee.api.ChatColor.MAGIC))
                        decorations.add(TextDecoration.OBFUSCATED);
                } else {
                    color = TextColor.color(_color.getRGB());
                }
            }
        }
        if (decorations.isEmpty())
            return Style.style(color);
        else
            return Style.style(color, decorations.toArray(TextDecoration[]::new));
    }

    @SuppressWarnings("deprecation")
    @Nullable
    public static Style toKyoriStyle(@Nullable org.bukkit.ChatColor... chatColors) {
        if (chatColors == null)
            return null;
        TextColor color = null;
        ArrayList<TextDecoration> decorations = new ArrayList<>();
        for (org.bukkit.ChatColor chatColor : chatColors) {
            if (chatColor == org.bukkit.ChatColor.RESET) {
                color = null;
                decorations.clear();
            } else {
                if (chatColor.isFormat()) {
                    switch (chatColor) {
                        case ITALIC -> decorations.add(TextDecoration.ITALIC);
                        case BOLD -> decorations.add(TextDecoration.BOLD);
                        case STRIKETHROUGH -> decorations.add(TextDecoration.STRIKETHROUGH);
                        case UNDERLINE -> decorations.add(TextDecoration.UNDERLINED);
                        case MAGIC -> decorations.add(TextDecoration.OBFUSCATED);
                        default -> {
                        }
                    }
                } else if (chatColor.isColor()) {
                    switch (chatColor) {
                        case AQUA -> color = NamedTextColor.AQUA;
                        case BLACK -> color = NamedTextColor.BLACK;
                        case BLUE -> color = NamedTextColor.BLUE;
                        case DARK_AQUA -> color = NamedTextColor.DARK_AQUA;
                        case DARK_BLUE -> color = NamedTextColor.DARK_BLUE;
                        case DARK_GRAY -> color = NamedTextColor.DARK_GRAY;
                        case DARK_GREEN -> color = NamedTextColor.DARK_GREEN;
                        case DARK_PURPLE -> color = NamedTextColor.DARK_PURPLE;
                        case DARK_RED -> color = NamedTextColor.DARK_RED;
                        case GOLD -> color = NamedTextColor.GOLD;
                        case GRAY -> color = NamedTextColor.GRAY;
                        case GREEN -> color = NamedTextColor.GREEN;
                        case LIGHT_PURPLE -> color = NamedTextColor.LIGHT_PURPLE;
                        case RED -> color = NamedTextColor.RED;
                        case WHITE -> color = NamedTextColor.WHITE;
                        case YELLOW -> color = NamedTextColor.YELLOW;
                        default -> {
                        }
                    }
                }
            }
        }
        if (decorations.isEmpty())
            return Style.style(color);
        else
            return Style.style(color, decorations.toArray(TextDecoration[]::new));
    }
}
