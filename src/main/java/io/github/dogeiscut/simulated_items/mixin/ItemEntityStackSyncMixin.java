package io.github.dogeiscut.simulated_items.mixin;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import io.github.dogeiscut.simulated_items.content.subLevelItem.SubLevelItemBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityStackSyncMixin {

    @Inject(method = "setItem", at = @At("TAIL"))
    private void ssi$syncSubLevelStack(ItemStack stack, CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (self.level().isClientSide) return;
        if (!self.getPersistentData().hasUUID("ssi_sublevel")) return;

        ServerLevel level = (ServerLevel) self.level();
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null) return;

        if (!(container.getSubLevel(self.getPersistentData().getUUID("ssi_sublevel")) instanceof ServerSubLevel subLevel))
            return;

        if (subLevel.getPlot().getEmbeddedLevelAccessor().getBlockEntity(BlockPos.ZERO) instanceof SubLevelItemBlockEntity be) {
            be.setItemStack(stack);
        }
    }
}