package ru.feytox.etherology.model.custom;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.magic.staff.StaffPartsInfo;
import ru.feytox.etherology.model.EtherologyModels;
import ru.feytox.etherology.model.ModelTransformations;
import ru.feytox.etherology.model.MultiItemModel;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class StaffModel extends MultiItemModel {

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        BakedModelManager modelManager = MinecraftClient.getInstance().getBakedModelManager();
        var modelConsumer = context.bakedModelConsumer();

        NbtCompound stackNbt = stack.getNbt();
        if (stackNbt == null) return;
        NbtList nbtList = stackNbt.getOr(StaffPartsInfo.LIST_KEY, new NbtList());
        nbtList.stream()
                .map(nbtElement -> {
                    if (nbtElement instanceof NbtCompound compound) return compound;
                    Etherology.ELOGGER.error("Found a non-NbtCompound element while loading EtherStaff NBT");
                    return null;
                })
                .filter(Objects::nonNull)
                .map(nbt -> nbt.get(StaffPartsInfo.NBT_KEY))
                .map(StaffPartsInfo::toModelId)
                .map(modelManager::getModel)
                .forEach(modelConsumer);
    }

    public static void loadPartModels(Consumer<Identifier> idConsumer) {
        StaffPartsInfo.generateAll().stream().map(StaffPartsInfo::toModelId).forEach(idConsumer);
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelTransformations.DEFAULT_ITEM_TRANSFORMS;
    }

    @Override
    public ModelIdentifier getModelForParticles() {
        return EtherologyModels.createItemModelId("staff_core");
    }

    @Override
    protected List<ModelIdentifier> getModels() {
        return ObjectArrayList.of();
    }
}
