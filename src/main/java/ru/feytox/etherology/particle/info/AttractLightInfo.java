package ru.feytox.etherology.particle.info;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.particle.LightParticle;
import ru.feytox.etherology.particle.types.LightParticleEffect;
import ru.feytox.etherology.util.feyapi.FeyColor;
import ru.feytox.etherology.util.feyapi.RGBColor;

public class AttractLightInfo extends PushingLightInfo {
    public AttractLightInfo(ClientWorld clientWorld, double x, double y, double z, LightParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
    }

    @Override
    public @Nullable RGBColor getStartColor(Random random) {
        return FeyColor.getRandomColor(RGBColor.of(0xCF70FF), RGBColor.of(0xCC3FFF), random);
    }

    @Override
    public void tick(LightParticle particle) {
        particle.simpleMovingTick(0.025f, endPos, true);
        particle.setSpriteForAge();
    }
}