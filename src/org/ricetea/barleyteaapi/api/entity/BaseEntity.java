package org.ricetea.barleyteaapi.api.entity;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.util.NamespacedKeyUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public abstract class BaseEntity implements Keyed {
    @Nonnull
    private static final NamespacedKey EntityTagNamespacedKey = NamespacedKeyUtils.BarleyTeaAPI("entity_id");
    @Nonnull
    private final NamespacedKey key;
    @Nonnull
    private final EntityType entityTypeBasedOn;

    public BaseEntity(@Nonnull NamespacedKey key, @Nonnull EntityType entityTypeBasedOn) {
        this.key = key;
        this.entityTypeBasedOn = entityTypeBasedOn;
    }

    @Nonnull
    public final NamespacedKey getKey() {
        return key;
    }

    @Nonnull
    public final String getNameInTranslateKey() {
        return "entity." + key.getNamespace() + "." + key.getKey() + ".name";
    }

    @Nonnull
    public String getDefaultName() {
        return getNameInTranslateKey();
    }

    @Nonnull
    public final EntityType getEntityTypeBasedOn() {
        return entityTypeBasedOn;
    }

    public final void register(@Nullable Entity entity) {
        if (entity != null)
            entity.getPersistentDataContainer().set(EntityTagNamespacedKey,
                    PersistentDataType.STRING, key.toString());
    }

    public final boolean isCertainEntity(@Nullable Entity entity) {
        return entity != null
                && key.toString().equals(entity.getPersistentDataContainer().getOrDefault(EntityTagNamespacedKey,
                        PersistentDataType.STRING, null));
    }

    public static void registerEntity(@Nullable Entity entity, @Nonnull BaseEntity entityType) {
        entityType.register(entity);
    }

    public static boolean isEntity(@Nullable Entity entity) {
        return entity != null && entity.getPersistentDataContainer().has(EntityTagNamespacedKey);
    }

    @Nullable
    public static NamespacedKey getEntityID(@Nullable Entity entity) {
        if (entity == null)
            return null;
        PersistentDataContainer container = entity.getPersistentDataContainer();
        String namespacedKeyString = container.getOrDefault(EntityTagNamespacedKey, PersistentDataType.STRING, null);
        return namespacedKeyString == null ? null
                : namespacedKeyString.contains(":") ? NamespacedKey.fromString(namespacedKeyString) : null;
    }

    public static boolean isCertainEntity(@Nullable Entity entity, @Nonnull BaseEntity entityType) {
        return entityType.isCertainEntity(entity);
    }

    @Nonnull
    public static DataEntityType getEntityType(Entity entity) {
        NamespacedKey entityTypeID = BaseEntity.getEntityID(entity);
        if (entityTypeID == null) {
            return DataEntityType.create(entity.getType());
        } else {
            BaseEntity baseEntity = EntityRegister.getInstance().lookupEntityType(entityTypeID);
            if (baseEntity == null)
                return DataEntityType.create(entity.getType());
            else
                return DataEntityType.create(baseEntity);
        }
    }

    @Deprecated
    protected final void setEntityName(@Nonnull Entity entity, @Nullable org.bukkit.ChatColor... colorAndStyles) {
        if (colorAndStyles == null) {
            setEntityName(entity);
        } else {
            TextColor color = null;
            ArrayList<TextDecoration> decorations = new ArrayList<>();
            for (org.bukkit.ChatColor chatColor : colorAndStyles) {
                if (chatColor == org.bukkit.ChatColor.RESET) {
                    color = null;
                    decorations.clear();
                } else {
                    if (chatColor.isFormat()) {
                        switch (chatColor) {
                            case ITALIC:
                                decorations.add(TextDecoration.ITALIC);
                                break;
                            case BOLD:
                                decorations.add(TextDecoration.BOLD);
                                break;
                            case STRIKETHROUGH:
                                decorations.add(TextDecoration.STRIKETHROUGH);
                                break;
                            case UNDERLINE:
                                decorations.add(TextDecoration.UNDERLINED);
                                break;
                            case MAGIC:
                                decorations.add(TextDecoration.OBFUSCATED);
                                break;
                            default:
                                break;
                        }
                    } else if (chatColor.isColor()) {
                        color = TextColor.color(chatColor.asBungee().getColor().getRGB());
                    }
                }
            }
            if (decorations.isEmpty()) {
                setEntityName(entity, color, (TextDecoration[]) null);
            } else {
                setEntityName(entity, color, decorations.toArray(TextDecoration[]::new));
            }
        }
    }

    @Deprecated
    protected final void setEntityName(@Nonnull Entity entity, @Nonnull String name,
            @Nullable org.bukkit.ChatColor... colorAndStyles) {
        if (colorAndStyles == null) {
            setEntityName(entity, name, null, (TextDecoration[]) null);
        } else {
            TextColor color = null;
            ArrayList<TextDecoration> decorations = new ArrayList<>();
            for (org.bukkit.ChatColor chatColor : colorAndStyles) {
                if (chatColor == org.bukkit.ChatColor.RESET) {
                    color = null;
                    decorations.clear();
                } else {
                    if (chatColor.isFormat()) {
                        switch (chatColor) {
                            case ITALIC:
                                decorations.add(TextDecoration.ITALIC);
                                break;
                            case BOLD:
                                decorations.add(TextDecoration.BOLD);
                                break;
                            case STRIKETHROUGH:
                                decorations.add(TextDecoration.STRIKETHROUGH);
                                break;
                            case UNDERLINE:
                                decorations.add(TextDecoration.UNDERLINED);
                                break;
                            case MAGIC:
                                decorations.add(TextDecoration.OBFUSCATED);
                                break;
                            default:
                                break;
                        }
                    } else if (chatColor.isColor()) {
                        color = TextColor.color(chatColor.asBungee().getColor().getRGB());
                    }
                }
            }
            if (decorations.isEmpty()) {
                setEntityName(entity, name, color, (TextDecoration[]) null);
            } else {
                setEntityName(entity, name, color, decorations.toArray(TextDecoration[]::new));
            }
        }
    }

    protected final void setEntityName(@Nonnull Entity entity,
            @Nullable net.md_5.bungee.api.ChatColor... colorAndStyles) {
        if (colorAndStyles == null) {
            setEntityName(entity, null, (TextDecoration[]) null);
        } else {
            TextColor color = null;
            ArrayList<TextDecoration> decorations = new ArrayList<>();
            for (net.md_5.bungee.api.ChatColor chatColor : colorAndStyles) {
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
            if (decorations.isEmpty()) {
                setEntityName(entity, color, (TextDecoration[]) null);
            } else {
                setEntityName(entity, color, decorations.toArray(TextDecoration[]::new));
            }
        }
    }

    protected final void setEntityName(@Nonnull Entity entity, @Nonnull String name,
            @Nullable net.md_5.bungee.api.ChatColor... colorAndStyles) {
        if (colorAndStyles == null) {
            setEntityName(entity, name, null, (TextDecoration[]) null);
        } else {
            TextColor color = null;
            ArrayList<TextDecoration> decorations = new ArrayList<>();
            for (net.md_5.bungee.api.ChatColor chatColor : colorAndStyles) {
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
            if (decorations.isEmpty()) {
                setEntityName(entity, name, color, (TextDecoration[]) null);
            } else {
                setEntityName(entity, name, color, decorations.toArray(TextDecoration[]::new));
            }
        }
    }

    @SuppressWarnings("null")
    protected final void setEntityName(@Nonnull Entity entity) {
        setEntityName(entity, Component.translatable(getNameInTranslateKey(), getDefaultName()));
    }

    @SuppressWarnings("null")
    protected final void setEntityName(@Nonnull Entity entity, @Nullable TextColor color,
            @Nullable TextDecoration... decoration) {
        setEntityName(entity,
                Component.translatable(getNameInTranslateKey(), getDefaultName(), Style.style(color, decoration)));
    }

    protected final void setEntityName(@Nonnull Entity entity, @Nonnull String name) {
        setEntityName(entity, name, null, (TextDecoration[]) null);
    }

    protected final void setEntityName(@Nonnull Entity entity, @Nonnull String name, @Nullable TextColor color,
            @Nullable TextDecoration... decorations) {
        entity.customName(decorations == null ? Component.text(name, color) : Component.text(name, color, decorations));
    }

    protected final void setEntityName(@Nonnull Entity entity, @Nonnull Component component) {
        entity.customName(component);
    }
}
