package io.github.dogeiscut.simulated_items.event;

import dev.ryanhcode.sable.api.sublevel.SubLevelObserver;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class ItemSubLevelSubLevelWatcher implements SubLevelObserver {

    public ItemSubLevelSubLevelWatcher() {
    }

    @Override
    public void onSubLevelRemoved(final SubLevel subLevel, final SubLevelRemovalReason reason) {
        ServerSubLevel serverSubLevel = (ServerSubLevel) subLevel;
        if (reason == SubLevelRemovalReason.REMOVED) {
            if (serverSubLevel.getUserDataTag() != null && serverSubLevel.getUserDataTag().hasUUID("ssi_item")) {
                Entity entity = serverSubLevel.getLevel().getEntity(serverSubLevel.getUserDataTag().getUUID("ssi_item"));
                if (entity != null) {
                    entity.remove(Entity.RemovalReason.DISCARDED);
                }
            }
        }
    }
}
