package ru.feytox.etherology.block.peach;

import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import ru.feytox.etherology.world.EConfiguredFeatures;

public class PeachSaplingGenerator extends SaplingGenerator {

    @Override
    protected RegistryKey<ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
        return EConfiguredFeatures.PEACH_SAPLING_TREE;
    }
}
