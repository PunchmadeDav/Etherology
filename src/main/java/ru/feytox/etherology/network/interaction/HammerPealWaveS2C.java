package ru.feytox.etherology.network.interaction;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.network.util.S2CPacketInfo;
import ru.feytox.etherology.particle.PealWaveParticle;
import ru.feytox.etherology.particle.types.args.ParticleArg;
import ru.feytox.etherology.particle.types.args.SimpleArgs;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class HammerPealWaveS2C extends AbstractS2CPacket {
    public static final Identifier HAMMER_PEAL_S2C_ID = new EIdentifier("hammer_peal_s2c");
    private final Vec3d fromPos;
    private final Vec3d toPos;

    public HammerPealWaveS2C(Vec3d fromPos, Vec3d toPos) {
        this.fromPos = fromPos;
        this.toPos = toPos;
    }

    public static void receive(S2CPacketInfo packetInfo) {
        PacketByteBuf buf = packetInfo.buf();
        MinecraftClient client = packetInfo.client();
        ClientWorld world = client.world;
        if (world == null) return;

        ParticleArg<Vec3d> vec3dArg = SimpleArgs.VEC3D.get();
        Vec3d fromPos = vec3dArg.read(buf);
        Vec3d toPos = vec3dArg.read(buf);

        client.execute(() -> PealWaveParticle.spawnWave(world, fromPos, toPos));
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf) {
        ParticleArg<Vec3d> vec3dArg = SimpleArgs.VEC3D.get();
        vec3dArg.write(buf, fromPos);
        vec3dArg.write(buf, toPos);
        return buf;
    }

    @Override
    public Identifier getPacketID() {
        return HAMMER_PEAL_S2C_ID;
    }
}