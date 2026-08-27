package io.github.dogeiscut.simulated_items.event;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

public class ItemSubLevelRemover {

    @SubscribeEvent
    public void onItemLeave(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide) return;
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) return;

        ServerLevel level = (ServerLevel) event.getLevel();
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);

        assert container != null;
        if (itemEntity.getPersistentData().hasUUID("ssi_sublevel")) {
            ServerSubLevel subLevel = (ServerSubLevel) container.getSubLevel(itemEntity.getPersistentData().getUUID("ssi_sublevel"));

            if (subLevel != null) {
                subLevel.markRemoved();
            }
        }
    }
}
