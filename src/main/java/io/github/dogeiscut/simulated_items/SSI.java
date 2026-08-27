package io.github.dogeiscut.simulated_items;

import com.mojang.logging.LogUtils;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.platform.SableEventPlatform;
import io.github.dogeiscut.simulated_items.event.ItemSubLevelSpawner;
import io.github.dogeiscut.simulated_items.event.ItemSubLevelRemover;
import io.github.dogeiscut.simulated_items.event.ItemSubLevelVelocityApplier;
import io.github.dogeiscut.simulated_items.event.SubLevelItemPlacementGuard;
import io.github.dogeiscut.simulated_items.event.ItemSubLevelSubLevelObserver;
import io.github.dogeiscut.simulated_items.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(SSI.ID)
public class SSI {
    public static final String ID = "simulated_items";
    public static final String NAME = "Sable: Simulated Items";

    public static final Logger LOGGER = LogUtils.getLogger();


    public SSI(IEventBus modEventBus, ModContainer modContainer) {
        onCtor(modEventBus, modContainer);
    }

    public static void onCtor(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("{} initializing!", NAME);

        SSIBlockEntities.register(modEventBus);
        SSIBlocks.register(modEventBus);

        SableEventPlatform.INSTANCE.onSubLevelContainerReady(
                (level, container) -> {
                    if (!(container instanceof ServerSubLevelContainer serverContainer)) return;

                    serverContainer.addObserver(new ItemSubLevelSubLevelObserver());
                }
        );

        NeoForge.EVENT_BUS.register(new ItemSubLevelSpawner());
        NeoForge.EVENT_BUS.register(new ItemSubLevelRemover());
        NeoForge.EVENT_BUS.register(new ItemSubLevelVelocityApplier());
        NeoForge.EVENT_BUS.register(new SubLevelItemPlacementGuard());
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }
}
