package ru.feytox.etherology.mixin;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.model.EtherologyModelProvider;
import ru.feytox.etherology.model.EtherologyModels;
import ru.feytox.etherology.registry.item.ToolItems;

import java.util.Arrays;
import java.util.Map;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {

    @Shadow protected abstract void addModel(ModelIdentifier modelId);

    @SuppressWarnings("rawtypes")
    @Inject(method = "<init>(Lnet/minecraft/client/color/block/BlockColors;Lnet/minecraft/util/profiler/Profiler;Ljava/util/Map;Ljava/util/Map;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;addModel(Lnet/minecraft/client/util/ModelIdentifier;)V", ordinal = 2))
    private void injectInHandModels(BlockColors blockColors, Profiler profiler, Map jsonUnbakedModels, Map blockStates, CallbackInfo ci) {
        // TODO: 02.01.2024 simplify
        Arrays.stream(ToolItems.GLAIVES).map(item -> EtherologyModels.getReplacedModel(item, true)).forEach(this::addModel);
        addModel(EtherologyModels.getReplacedModel(ToolItems.OCULUS, true));
        addModel(EtherologyModelProvider.STAFF);
        addModel(EtherologyModelProvider.STAFF_STREAM);
        addModel(EtherologyModelProvider.STAFF_CHARGE);
    }
}
