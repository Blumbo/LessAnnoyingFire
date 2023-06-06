package net.blumbo.lessannoyingfire.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow @Nullable private DamageSource lastDamageSource;

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    DamageSource damageSource;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "damage", at = @At(value = "FIELD", ordinal = 0, opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/LivingEntity;timeUntilRegen:I"))
    private void setSource1(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        damageSource = source;
    }

    @Redirect(method = "damage", at = @At(value = "FIELD", ordinal = 0, opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/LivingEntity;timeUntilRegen:I"))
    private int uh(LivingEntity value) {
        // Make fire caused invulnerability ticks irrelevant if damage comes from an entity
        if (fireDamageSource(value, lastDamageSource) && damageSource.getSource() != null) return 0;
        return timeUntilRegen;
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;scheduleVelocityUpdate()V"))
    private void setSource2(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        damageSource = source;
    }

    @Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;scheduleVelocityUpdate()V"))
    private void yes(LivingEntity instance) {
        // Prevent fire from messing up movement
        if (!fireDamageSource(instance, damageSource)) scheduleVelocityUpdate();

    }

    private static boolean fireDamageSource(LivingEntity entity, DamageSource damageSource) {
        DamageSources damageSources = entity.getWorld().getDamageSources();
        return damageSource == damageSources.onFire() || damageSource == damageSources.inFire();
    }

}
