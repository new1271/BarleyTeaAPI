package org.ricetea.barleyteaapi.internal.helper;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Tag;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class MaterialHelper {

    @Nullable
    public static TrimPattern getTrimPatternByMaterial(@Nullable Material material) {
        if (material == null)
            return null;
        if (Tag.ITEMS_TRIM_TEMPLATES.getValues().contains(material)) {
            String key = material.getKey().getKey();
            String endWithString = "_armor_trim_smithing_template";
            if (key.toLowerCase().endsWith("_armor_trim_smithing_template")) {
                key = key.substring(0, key.length() - endWithString.length() - 1);
                try {
                    return (TrimPattern) (TrimPattern) Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(key));
                } catch (Exception e) {
                }
            }
        }
        return null;
    }
}
