package io.github.dogeiscut.simulated_items.content.subLevelItem;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class SubLevelItemRenderer implements BlockEntityRenderer<SubLevelItemBlockEntity> {

    private final RandomSource random = RandomSource.create();

    public SubLevelItemRenderer(BlockEntityRendererProvider.Context context) {}

    private int getRenderAmount(int count) {
        if (count <= 1) return 1;
        if (count <= 16) return 2;


        if (count <= 32) return 3;
        if (count <= 48) return 4;
        return 5;
    }

    @Override
    public void render(SubLevelItemBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ItemStack itemStack = be.getItemStack();
        if (itemStack == null || itemStack.isEmpty())
            return;

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        int seed = ItemEntityRenderer.getSeedForItemStack(itemStack);
        BakedModel bakedModel = itemRenderer.getModel(itemStack, be.getLevel(), null, 0);
        boolean isGui3d = bakedModel.isGui3d();

        ms.pushPose();

        ms.translate(0.5D, 0.5D, 0.5D);

        float scale = isGui3d ? 0.75f : 0.5f;

        this.random.setSeed(seed);

        int renderCount = getRenderAmount(itemStack.getCount());
        boolean shouldSpread = IClientItemExtensions.of(itemStack).shouldSpreadAsEntity(itemStack);

        for (int j = 0; j < renderCount; j++) {
            ms.pushPose();

            if (j > 0 && shouldSpread) {
                if (isGui3d) {
                    float offsetX = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float offsetY = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float offsetZ = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    ms.translate(offsetX, offsetY, offsetZ);
                } else {
                    float offsetX = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    float offsetY = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;

                    ms.translate(offsetX, offsetY, j * 0.01F);
                }
            }
            ms.scale(scale, scale, scale);
            itemRenderer.render(itemStack, ItemDisplayContext.FIXED, false, ms, buffer, light, overlay, bakedModel);

            ms.popPose();
        }
        ms.popPose();
    }
}
