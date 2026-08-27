package io.github.dogeiscut.simulated_items.event;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.neoforge.event.ForgeSablePostPhysicsTickEvent;
import dev.ryanhcode.sable.neoforge.event.ForgeSablePrePhysicsTickEvent;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import org.joml.Vector3d;

import java.util.Iterator;

public class ItemSublevelItemAttacher {

    @SubscribeEvent
    public void onSablePrePhysicsTick(ForgeSablePostPhysicsTickEvent event) {
        SubLevelPhysicsSystem physicsSystem = event.getPhysicsSystem();
        ServerSubLevelContainer container = SubLevelContainer.getContainer(physicsSystem.getLevel());
        if (container == null) return;

        final ServerLevel level = physicsSystem.getLevel();

        Iterator<ServerSubLevel> iterator = container.getAllSubLevels().iterator();

        // TODO: this looks pretty bad (as evident by the ItemEntity shadow) and probably runs poorly too
        // Find a better way to attach item entities to sublevels (maybe through tracking somehow?)
        while (iterator.hasNext()) {
            ServerSubLevel serverSubLevel = iterator.next();
            if (serverSubLevel.getUserDataTag() != null && serverSubLevel.getUserDataTag().hasUUID("ssi_item")) {
                ItemEntity itemEntity = (ItemEntity) level.getEntity(serverSubLevel.getUserDataTag().getUUID("ssi_item"));
                if (itemEntity != null) {
                    Vector3d position = serverSubLevel.logicalPose().position();
                    Vector3d velocity = serverSubLevel.latestLinearVelocity;
                    itemEntity.setPos(new Vec3(position.x, position.y, position.z));
                    itemEntity.setDeltaMovement(new Vec3(velocity.x, velocity.y, velocity.z).scale(1.0/16.0));
                }
            }
        };
    }
}
