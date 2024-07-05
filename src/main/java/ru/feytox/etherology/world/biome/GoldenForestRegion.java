package ru.feytox.etherology.world.biome;

import com.mojang.datafixers.util.Pair;
import lombok.val;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import ru.feytox.etherology.util.misc.EIdentifier;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;

import java.util.function.Consumer;

import static terrablender.api.ParameterUtils.*;

public class GoldenForestRegion extends Region {

    public GoldenForestRegion() {
        super(new EIdentifier("golden_forest_region"), RegionType.OVERWORLD, 3);
    }

    // TODO: 25.06.2024 remove this comment
    // /execute positioned ~2500 ~ ~2500 run locate biome etherology:golden_forest
    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> mapper) {
        val builder = new VanillaParameterOverlayBuilder();

        new ParameterPointListBuilder()
                .temperature(Temperature.NEUTRAL)
                .humidity(Humidity.NEUTRAL)
                .continentalness(Continentalness.MID_INLAND, Continentalness.FAR_INLAND)
                .erosion(Erosion.span(Erosion.EROSION_5, Erosion.EROSION_6))
                .depth(Depth.SURFACE, Depth.FLOOR)
                .weirdness(Weirdness.LOW_SLICE_NORMAL_DESCENDING)
                .build().forEach(point -> builder.add(point, EtherBiomes.GOLDEN_FOREST));

        builder.build().forEach(mapper);
    }
}