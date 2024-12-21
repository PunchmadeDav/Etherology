package ru.feytox.etherology.client.model;

import lombok.experimental.UtilityClass;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.magic.staff.StaffPartInfo;
import ru.feytox.etherology.util.misc.DoubleModel;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.Optional;

@UtilityClass
public class EtherologyModels {

    public static final TextureKey STYLE = TextureKey.of("style");

    private static Model item(String parent, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(EIdentifier.of("item/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    @Nullable
    public static ModelIdentifier getReplacedModel(Item item, boolean isInHand) {
        if (!(item instanceof DoubleModel)) return null;
        String modelPath = item + (isInHand ? "_in_hand" : "");
        return new ModelIdentifier(Identifier.of(modelPath), "inventory");
    }

    public static ModelIdentifier createItemModelId(String modelPath) {
        return new ModelIdentifier(EIdentifier.of(modelPath), "inventory");
    }

    public static Model getStaffPartModel(StaffPartInfo partInfo) {
        StaffPart part = partInfo.part();
        if (!part.isStyled()) return item("staff_" + part.getName(), TextureKey.PARTICLE, STYLE);
        return item("staff_" + part.getName() + "_" + partInfo.firstPattern().getName(), TextureKey.PARTICLE, STYLE);
    }
}
