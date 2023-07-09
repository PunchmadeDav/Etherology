package ru.feytox.etherology.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.enums.LightParticleType;
import ru.feytox.etherology.particle.types.LightParticleEffect;
import ru.feytox.etherology.particle.utility.MovingParticle;
import ru.feytox.etherology.util.feyapi.RGBColor;

// TODO: 21.06.2023 simplify
public class LightParticle extends MovingParticle<LightParticleEffect> {
    private final int startRed;
    private final int startGreen;
    private final int startBlue;
    private final LightParticleType lightType;
    private final Vec3d endPos;

    public LightParticle(ClientWorld clientWorld, double x, double y, double z, LightParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        this.lightType = parameters.getLightType();

        // TODO: 09.07.2023 particle builders

        Vec3d moveVec = parameters.getMoveVec();
        RGBColor color = lightType.getColor(random);
        switch (lightType) {
            case SIMPLE -> {
                setSprite(spriteProvider);
                endPos = moveVec;
            }
            case SPARK -> {
                this.scale(0.1f);
                setSprite(spriteProvider);
                endPos = moveVec;
            }
            case BREWING -> {
                setSpriteForAge(spriteProvider);
                maxAge = 20;
                this.scale(0.5f);
                endPos = startPos.add(moveVec);
            }
            case ATTRACT, PUSHING -> {
                setSpriteForAge(spriteProvider);
                maxAge = 40;
                endPos = startPos.add(moveVec);
            }
            default -> endPos = moveVec;
        }
        if (color != null) {
            setRGB(color);
            this.scale(0.3f);
        }

        this.startRed = MathHelper.floor(this.red * 255);
        this.startGreen = MathHelper.floor(this.green * 255);
        this.startBlue = MathHelper.floor(this.blue * 255);
    }

    @Override
    public void tick() {
        if (!lightType.equals(LightParticleType.SPARK)) {
            boolean isSimple = lightType.equals(LightParticleType.SIMPLE);
            if (!isSimple) {
                simpleMovingTick(0.025f, endPos, lightType.equals(LightParticleType.ATTRACT));
                setSpriteForAge(spriteProvider);
            }
            else acceleratedMovingTick(0.1f, 0.5f, true, endPos);
            return;
        }

        acceleratedMovingTick(0.4f, 0.5f, true, endPos);
        double pathLen = getPathVec(endPos).length();
        double fullPathLen = getFullPathVec(endPos).length();
        this.setRGB(startRed + (83 - startRed) * ((fullPathLen - pathLen) / fullPathLen),
                startGreen + (14 - startGreen) * ((fullPathLen - pathLen) / fullPathLen),
                startBlue + (255 - startBlue) * ((fullPathLen - pathLen) / fullPathLen));
    }
}
