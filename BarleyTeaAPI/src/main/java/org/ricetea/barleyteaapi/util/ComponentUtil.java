package org.ricetea.barleyteaapi.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class ComponentUtil {

    public static boolean translatableComponentEquals(@Nullable Component a, @Nullable Component b) {
        if (a instanceof TranslatableComponent translatableComponentA
                && b instanceof TranslatableComponent translatableComponentB) {
            if (translatableComponentA.key().equals(translatableComponentB.key())) {
                List<Component> argsA = translatableComponentA.args();
                int needCheck = argsA.size();
                if (needCheck > 0) {
                    ArrayList<Component> argsB = new ArrayList<>(translatableComponentB.args());
                    for (Component argA : argsA) {
                        for (var iterator = argsB.iterator(); iterator.hasNext(); ) {
                            Component argB = iterator.next();
                            if (translatableComponentEquals(argA, argB)) {
                                iterator.remove();
                                needCheck--;
                                break;
                            }
                        }
                    }
                    return needCheck <= 0 && argsB.size() <= 0;
                } else {
                    return translatableComponentB.args().size() <= 0;
                }
            } else {
                return false;
            }
        }
        return Component.EQUALS.test(a, b);
    }
}
