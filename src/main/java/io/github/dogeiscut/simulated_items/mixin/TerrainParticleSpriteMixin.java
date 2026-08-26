package io.github.dogeiscut.simulated_items.mixin;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import io.github.dogeiscut.simulated_items.SSI;
import io.github.dogeiscut.simulated_items.content.subLevelItem.SubLevelItemBlock;
import io.github.dogeiscut.simulated_items.content.subLevelItem.SubLevelItemBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TerrainParticle.class)
public abstract class TerrainParticleSpriteMixin extends TextureSheetParticle {

    @Shadow @Final private BlockPos pos;

    protected TerrainParticleSpriteMixin(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    // TODO: fix for sublevels :/
    @Inject(method = "updateSprite", at = @At("HEAD"), cancellable = true)
    private void ssi$overrideItemSprite(BlockState state, BlockPos blockPos, CallbackInfoReturnable<TerrainParticle> cir) {
        try {
            if (!(state.getBlock() instanceof SubLevelItemBlock)) return;
            if (this.level == null) return;

            SSI.LOGGER.info("Blockpos {}", blockPos);
            SSI.LOGGER.info("pos {}", pos);

            if (!(this.level.getBlockEntity(blockPos) instanceof SubLevelItemBlockEntity be)) return;

            ItemStack stack = be.getItemStack();
            if (stack.isEmpty()) return;

            BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(stack, this.level, null, 0);
            this.setSprite(model.getParticleIcon());
            cir.setReturnValue((TerrainParticle) (Object) this);
        } catch (Exception e) {
            SSI.LOGGER.error("Failed to override item sprite for block {} at position {}", state, blockPos, e);
        }
    }
}