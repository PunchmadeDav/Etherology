package ru.feytox.etherology.item;

import lombok.val;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import ru.feytox.etherology.entity.redstoneBlob.RedstoneChargeEntity;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.magic.lens.LensModifier;
import ru.feytox.etherology.magic.lens.RedstoneLensEffects;
import ru.feytox.etherology.magic.staff.StaffLenses;
import ru.feytox.etherology.network.interaction.RedstoneLensStreamS2C;
import ru.feytox.etherology.registry.item.ToolItems;

import java.util.function.Supplier;

public class RedstoneLens extends LensItem {

    public RedstoneLens() {
        super(StaffLenses.REDSTONE, 1.0f, 1.0f);
    }

    @Override
    public boolean onStreamUse(World world, LivingEntity entity, LensComponent lensData, ItemStack lensStack, boolean hold, Supplier<Hand> handGetter) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) return false;

        if (!lensData.checkCooldown(serverWorld)) return false;

        float maxDistance = lensData.calcValue(LensModifier.AREA, 40, 80, 0.8f);
        HitResult hitResult = entity.raycast(maxDistance, 1.0f, false);
        if (!(hitResult instanceof BlockHitResult blockHitResult)) return false;
        if (!hold) entity.setCurrentHand(handGetter.get());
        if (LensItem.decrementEther(entity, lensStack, lensData)) return false;

        int cooldown = getStreamCooldown(lensData);
        lensData.incrementCooldown(serverWorld, cooldown);

        boolean isMiss = !hitResult.getType().equals(HitResult.Type.BLOCK);
        if (!isMiss) {
            BlockPos hitPos = blockHitResult.getBlockPos();
            int power = getPower(lensData);
            RedstoneLensEffects.getServerState(serverWorld).addUsage(serverWorld, hitPos, power, 10);
        }

        boolean isDamaged = LensItem.damageLens(lensStack, 1);
        Vec3d startPos = entity.getBoundingBox().getCenter().add(entity.getHandPosOffset(ToolItems.STAFF));
        val packet = new RedstoneLensStreamS2C(startPos, blockHitResult.getPos(), isMiss);
        if (entity instanceof ServerPlayerEntity player) {
            packet.sendForTrackingAndSelf(player);
            return isDamaged;
        }

        packet.sendForTracking(serverWorld, entity.getBlockPos(), 0);
        return isDamaged;
    }

    @Override
    public boolean onChargeUse(World world, LivingEntity entity, LensComponent lensData, ItemStack lensStack, boolean hold, Supplier<Hand> handGetter) {
        if (world.isClient) return false;
        if (hold) return false;

        entity.setCurrentHand(handGetter.get());
        return false;
    }

    @Override
    public boolean onChargeStop(World world, LivingEntity entity, LensComponent lensData, ItemStack lensStack, int holdTicks, Supplier<Hand> handGetter) {
        if (world.isClient) {
            entity.swingHand(handGetter.get());
            return false;
        }

        if (holdTicks == 0) return false;
        if (LensItem.decrementEther(entity, lensStack, lensData)) return false;

        Vec3d entityRotation = entity.getRotationVec(0.1f);
        Vec3d chargePos = entity.getBoundingBox().getCenter();
        float speed = lensData.calcValue(LensModifier.STREAM, 1, 5, 0.75f);
        int power = getPower(lensData);

        int maxAge = lensData.calcRoundValue(LensModifier.AREA, 100, 300, 0.8f);
        RedstoneChargeEntity blob = new RedstoneChargeEntity(world, chargePos.x, chargePos.y, chargePos.z, entityRotation, power, holdTicks, speed, maxAge);
        world.spawnEntity(blob);
        return LensItem.damageLens(lensStack, 1);
    }

    private int getPower(LensComponent lensData) {
        return lensData.calcRoundValue(LensModifier.REINFORCEMENT, 3, 15, 0.6f);
    }
}
