package ru.feytox.etherology.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import ru.feytox.etherology.datagen.lang.RuLangProvider;
import ru.feytox.etherology.world.EConfiguredFeatures;
import ru.feytox.etherology.world.EPlacedFeatures;

public class DataGeneration implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(WorldGeneration::new);
        pack.addProvider(ModelGeneration::new);
        pack.addProvider(RuLangProvider::new);
        pack.addProvider(RecipeGeneration::new);
        BlockTagGeneration blockTagGeneration = pack.addProvider(BlockTagGeneration::new);
        pack.addProvider(((output, registries) -> new ItemTagGeneration(output, registries, blockTagGeneration)));
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, EConfiguredFeatures::bootstrap);
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, EPlacedFeatures::bootstrap);
    }
}
