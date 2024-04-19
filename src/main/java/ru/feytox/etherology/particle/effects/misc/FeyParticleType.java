package ru.feytox.etherology.particle.effects.misc;

import com.mojang.serialization.Codec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class FeyParticleType<T extends ParticleEffect> extends ParticleType<T> {

    private final Codec<T> codec;
    private final ParticleEffect.Factory<T> dummyFactory;

    public FeyParticleType(boolean alwaysShow, FeyParticleEffect.DummyConstructor<T> dummyConstructor) {
        super(alwaysShow, null);
        FeyParticleEffect<T> dummy = dummyConstructor.createDummy(this);
        codec = dummy.createCodec();
        dummyFactory = dummy.createFactory();
    }

    @Override
    public Codec<T> getCodec() {
        return codec;
    }

    @Override
    public ParticleEffect.Factory<T> getParametersFactory() {
        return dummyFactory;
    }
}
