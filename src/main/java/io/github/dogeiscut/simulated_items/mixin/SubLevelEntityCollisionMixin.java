package io.github.dogeiscut.simulated_items.mixin;

import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(dev.ryanhcode.sable.sublevel.entity_collision.SubLevelEntityCollision.class)
public class SubLevelEntityCollisionMixin {

    @ModifyVariable(
            method = "collide",
            at = @At(
                    value = "INVOKE",
                    target = "Lit/unimi/dsi/fastutil/objects/ObjectSet;iterator()Lit/unimi/dsi/fastutil/objects/ObjectIterator;",
                    remap = false
            ),
            remap = false
    )
    private static ObjectSet<SubLevel> modifyIntersecting(ObjectSet<SubLevel> original) {
        original.removeIf(subLevel->(
                // TODO: this solution doesn't work for the player as Minecraft movement is client sided. But non server sublevels dont provide getUserDataTag
                // TODO: make the collision removal temporary (for like 3 seconds or something)
                // TODO: the sublevel should just always NOT have collision with the item entity
            !subLevel.getLevel().isClientSide() &&
                    ((ServerSubLevel) subLevel).getUserDataTag() != null &&
                    ((ServerSubLevel) subLevel).getUserDataTag().hasUUID("ssi_item")
        ));
        return original;
    }
}