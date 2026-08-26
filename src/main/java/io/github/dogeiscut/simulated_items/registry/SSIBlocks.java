package io.github.dogeiscut.simulated_items.registry;

import io.github.dogeiscut.simulated_items.SSI;
import io.github.dogeiscut.simulated_items.content.subLevelItem.SubLevelItemBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SSIBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SSI.ID);

    public static final DeferredBlock<SubLevelItemBlock> SUB_LEVEL_ITEM = BLOCKS.registerBlock(
            "sub_level_item",
            SubLevelItemBlock::new,
            SubLevelItemBlock.defaultProperties()
    );

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}