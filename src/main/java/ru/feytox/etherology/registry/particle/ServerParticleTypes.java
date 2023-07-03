package ru.feytox.etherology.registry.particle;

import lombok.experimental.UtilityClass;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.particle.types.LightParticleEffect;
import ru.feytox.etherology.particle.types.MovingParticleEffect;
import ru.feytox.etherology.particle.types.misc.FeyParticleEffect;
import ru.feytox.etherology.particle.types.misc.FeyParticleType;
import ru.feytox.etherology.util.feyapi.EIdentifier;

@UtilityClass
public class ServerParticleTypes {
    public static final FeyParticleType<LightParticleEffect> LIGHT = register("light_new", false, LightParticleEffect::new);
    public static final FeyParticleType<MovingParticleEffect> THUNDER_ZAP = register("thunder_zap", false, MovingParticleEffect::new);

    private static <T extends ParticleEffect> FeyParticleType<T> register(String name, boolean alwaysShow, FeyParticleEffect.DummyConstructor<T> dummyConstructor) {
        FeyParticleType<T> particleType = new FeyParticleType<>(alwaysShow, dummyConstructor);
        return Registry.register(Registries.PARTICLE_TYPE, new EIdentifier(name), particleType);
    }
}