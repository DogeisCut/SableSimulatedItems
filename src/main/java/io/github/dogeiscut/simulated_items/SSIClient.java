package io.github.dogeiscut.simulated_items;

import io.github.dogeiscut.simulated_items.content.subLevelItem.SubLevelItemRenderer;
import io.github.dogeiscut.simulated_items.registry.SSIBlockEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = SSI.ID, dist = Dist.CLIENT)
public class SSIClient {
    public SSIClient(IEventBus modEventBus) {
        onCtorClient(modEventBus);
    }

    public static void onCtorClient(IEventBus modEventBus) {
        modEventBus.addListener(SSIClient::registerRenderers);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(SSIBlockEntities.SUB_LEVEL_ITEM.get(), SubLevelItemRenderer::new);
    }

}
