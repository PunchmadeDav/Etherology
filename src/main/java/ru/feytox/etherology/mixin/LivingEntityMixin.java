package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.feytox.etherology.item.BattlePickaxe;
import ru.feytox.etherology.item.IronShield;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @ModifyExpressionValue(method = "applyArmorToDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/DamageUtil;getDamageLeft(Lnet/minecraft/entity/LivingEntity;FLnet/minecraft/entity/damage/DamageSource;FF)F"))
    private float getDamageByPick(float original, @Local(argsOnly = true) DamageSource source) {
        if (!(source.getAttacker() instanceof LivingEntity entity)) return original;
        if (!(entity.getMainHandStack().getItem() instanceof BattlePickaxe pick)) return original;

        LivingEntity it = ((LivingEntity) (Object) this);
        original = Math.min(original + 0.5f * pick.getDamagePercent() * it.getArmor(), original * 1.5f);
        return original;
    }

    @ModifyExpressionValue(method = "modifyAppliedDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getProtectionAmount(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/damage/DamageSource;)F"))
    private float getProtectionOnPick(float original, @Local(argsOnly = true) DamageSource source) {
        if (!(source.getAttacker() instanceof LivingEntity entity)) return original;
        if (!(entity.getMainHandStack().getItem() instanceof BattlePickaxe pick)) return original;

        float k = pick.getDamagePercent();
        return Math.round(original * (1 - 0.5f * k));
    }

    @Inject(method = "blockedByShield", at = @At(value = "HEAD"), cancellable = true)
    private void onShieldBlocking(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity shieldHolder = ((LivingEntity) (Object) this);
        if (!(shieldHolder.getActiveItem().getItem() instanceof IronShield)) return;

        cir.setReturnValue(modifiedBlockedByShield(shieldHolder, source));
    }

    @Unique
    private static boolean modifiedBlockedByShield(LivingEntity shieldHolder, DamageSource source) {
        Entity entity = source.getSource();
        boolean isProjectile = entity instanceof ProjectileEntity;
        if (isProjectile && entity instanceof PersistentProjectileEntity persistentProjectile) {
            if (persistentProjectile.getPierceLevel() > 0) {
                return false;
            }
        }

        if (!source.isIn(DamageTypeTags.BYPASSES_ARMOR) && shieldHolder.isBlocking()) {
            Vec3d damagePos = source.getPosition();
            if (damagePos != null) {
                Vec3d holderRotation = shieldHolder.getRotationVec(1.0F);
                return IronShield.shieldBlockCheck(holderRotation, shieldHolder.getPos(), damagePos);
            }
        }

        return false;
    }
}
