package io.github.dogeiscut.simulated_items.event;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import io.github.dogeiscut.simulated_items.SSI;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

public class ItemSubLevelSpawner {

    @SubscribeEvent
    public static void onItemJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) return;
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) return;
        if (itemEntity.getPersistentData().getBoolean("ssi_converted")) return;

        // TODO: replace with the actual data-driven item/tag config
        if (!shouldConvert(itemEntity)) return;

        ServerLevel level = (ServerLevel) event.getLevel();
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);

//        SubLevel subLevel;
//        try {
//            subLevel = container.allocateNewSubLevel(new Pose3d(itemEntity.position(), new Quaterniond(0,0,0,1), new Vector3d(), new Vector3d(1.0,1.0,1.0)));
//        } catch (IllegalStateException e) {
//            return; // plot grid full - fall back to vanilla item, no config gate yet
//        }
//
//        // real block coordinates for this sub-level's plot, not the visual position
//        var plotOrigin = subLevel.getPlot().origin(); // TODO confirm actual accessor name
//        level.setBlockAndUpdate(plotOrigin, SSIBlocks.SUB_LEVEL_ITEM.get().defaultBlockState());
//        if (level.getBlockEntity(plotOrigin) instanceof SubLevelItemBlockEntity be) {
//            be.setItemStack(itemEntity.getItem().copy());
//        }

        // TODO: remove item collision
        itemEntity.setInvisible(true); // TODO: This doesn't work on items.
        itemEntity.getPersistentData().putBoolean("ssi_converted", true);
        //itemEntity.getPersistentData().putUUID("ssi_sublevel", subLevel.getUniqueId());

        // TODO next session: register itemEntity as tracking subLevel so it follows it,
        // and hook removal (pickup/despawn) to deallocate the sub-level via container.removeSubLevel(...)
    }

    private static boolean shouldConvert(ItemEntity itemEntity) {
        return true; // TODO: stub
    }
}