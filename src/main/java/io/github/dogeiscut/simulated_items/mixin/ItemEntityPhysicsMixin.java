package io.github.dogeiscut.simulated_items.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// This mixin is intentionally unregistered. Working on some physics things...
@Mixin(Entity.class)
public abstract class ItemEntityPhysicsMixin {

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    private void cancelItemMovement(MoverType type, Vec3 pos, CallbackInfo ci) {
        if ((Object) this instanceof ItemEntity) {
            ci.cancel();
        }
    }
}