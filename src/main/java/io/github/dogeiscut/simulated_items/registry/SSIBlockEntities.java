package io.github.dogeiscut.simulated_items.registry;

import io.github.dogeiscut.simulated_items.SSI;
import io.github.dogeiscut.simulated_items.content.subLevelItem.SubLevelItemBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SSIBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SSI.ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SubLevelItemBlockEntity>> SUB_LEVEL_ITEM =
            BLOCK_ENTITIES.register("sub_level_item", () ->
                    BlockEntityType.Builder.of(SubLevelItemBlockEntity::new, SSIBlocks.SUB_LEVEL_ITEM.get())
                            .build(com.mojang.datafixers.DSL.remainderType())
            );

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}