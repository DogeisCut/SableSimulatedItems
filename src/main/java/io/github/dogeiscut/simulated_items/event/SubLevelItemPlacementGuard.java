package io.github.dogeiscut.simulated_items.event;

import io.github.dogeiscut.simulated_items.content.subLevelItem.SubLevelItemBlock;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class SubLevelItemPlacementGuard {

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().getBlockState(event.getPos()).getBlock() instanceof SubLevelItemBlock
                && event.getItemStack().getItem() instanceof BlockItem) {
            event.setCanceled(true);
        }
    }
}
