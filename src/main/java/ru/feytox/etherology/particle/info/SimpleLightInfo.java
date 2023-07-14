package ru.feytox.etherology.particle.info;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.particle.LightParticle;
import ru.feytox.etherology.particle.types.LightParticleEffect;
import ru.feytox.etherology.particle.utility.ParticleInfo;
import ru.feytox.etherology.util.feyapi.RGBColor;

public class SimpleLightInfo extends ParticleInfo<LightParticle, LightParticleEffect> {
    private final Vec3d endPos;

    public SimpleLightInfo(ClientWorld clientWorld, double x, double y, double z, LightParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        endPos = parameters.getMoveVec();
    }

    @Override
    public void extraInit(LightParticle particle) {
        particle.setSprite(particle.getSpriteProvider());
    }

    @Override
    public RGBColor getStartColor(Random random) {
        return new RGBColor(244, 194, 133);
    }

    @Override
    public void tick(LightParticle particle) {
        particle.acceleratedMovingTick(0.1f, 0.5f, true, endPos);
    }

    @Override
    public int getMaxAge(Random random) {
        return 80;
    }
}
