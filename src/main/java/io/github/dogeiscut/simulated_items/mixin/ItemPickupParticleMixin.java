package io.github.dogeiscut.simulated_items.mixin;

import io.github.dogeiscut.simulated_items.client.PickupItemAccess;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemPickupParticle.class)
public class ItemPickupParticleMixin {

    @Inject(method = "getSafeCopy", at = @At("RETURN"))
    private void markCopiedItem(Entity entity, CallbackInfoReturnable<Entity> cir) {
        if (cir.getReturnValue() instanceof PickupItemAccess access) {
            access.simulated_items$setPickupItem(true);
        }
    }
}