package ru.feytox.etherology.util.misc;

import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import ru.feytox.etherology.registry.item.EItems;

public class BoatTypes {

    public static BoatEntity.Type PEACH;

    public static Item getBoatFromType(Item original, BoatEntity.Type type, boolean chest) {
        if (type.equals(PEACH)) return chest ? EItems.PEACH_CHEST_BOAT : EItems.PEACH_BOAT;
        return original;
    }

    static {
        BoatEntity.Type.values();
    }
}
