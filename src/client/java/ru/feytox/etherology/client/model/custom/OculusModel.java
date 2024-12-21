package ru.feytox.etherology.client.model.custom;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import ru.feytox.etherology.client.model.EtherologyModels;
import ru.feytox.etherology.client.model.ModelComponents;
import ru.feytox.etherology.client.model.MultiItemModel;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.List;

public class OculusModel extends MultiItemModel {
    public static final ModelIdentifier OCULUS_BASE = EtherologyModels.createItemModelId("item/oculus_base");
    public static final ModelIdentifier OCULUS_LENS = EtherologyModels.createItemModelId("item/oculus_lens");


    @Override
    public ModelIdentifier getModelForParticles() {
        return OCULUS_BASE;
    }

    @Override
    protected List<ModelIdentifier> getModels() {
        return ObjectArrayList.of(OCULUS_BASE, OCULUS_LENS);
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelComponents.loadTransformFromJson(EIdentifier.of("models/item/oculus_base"));
    }
}
