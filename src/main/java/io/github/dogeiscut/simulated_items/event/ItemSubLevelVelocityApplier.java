package io.github.dogeiscut.simulated_items.event;

import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.neoforge.event.ForgeSablePrePhysicsTickEvent;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import org.joml.Vector3d;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * TODO: leaky and lost on save+quit
 * a tick-perfect drop can lose its pending velocity entirely, since this set only lives in memory.
 * Look at SubLevelObserver#onSubLevelAdded as way to derive this from the sub-level's own persisted
 * tag instead of tracking it here.
 */
public class ItemSubLevelVelocityApplier {

    private static final double EPSILON = 0.02;
    private static final Set<UUID> pendingVelocity = new HashSet<>();

    public static void markPending(UUID subLevelId) {
        pendingVelocity.add(subLevelId);
    }

    @SubscribeEvent
    public void onSablePrePhysicsTick(ForgeSablePrePhysicsTickEvent event) {
        if (pendingVelocity.isEmpty()) return;
        SubLevelPhysicsSystem physicsSystem = event.getPhysicsSystem();
        ServerSubLevelContainer container = SubLevelContainer.getContainer(physicsSystem.getLevel());
        if (container == null) return;

        final ServerLevel level = physicsSystem.getLevel();

        pendingVelocity.removeIf(id -> {
            if (!(container.getSubLevel(id) instanceof ServerSubLevel subLevel)) return true;
            CompoundTag tag = subLevel.getUserDataTag();
            if (tag == null || !tag.contains("ssi_item_velocity_x")) return true;

            RigidBodyHandle handle = physicsSystem.getPhysicsHandle(subLevel);
            if (handle != null) {
                Vector3d linearVelocity = new Vector3d(
                        tag.getDouble("ssi_item_velocity_x"),
                        tag.getDouble("ssi_item_velocity_y"),
                        tag.getDouble("ssi_item_velocity_z")).mul(16.0);

                double speed = linearVelocity.length();
                Vector3d angularVelocity = new Vector3d();
                if (speed > EPSILON) {
                    angularVelocity.set(
                            level.random.nextDouble() * 2 - 1,
                            level.random.nextDouble() * 2 - 1,
                            level.random.nextDouble() * 2 - 1
                    ).normalize().mul(speed);
                }

                handle.addLinearAndAngularVelocity(linearVelocity, angularVelocity);
            }
            tag.remove("ssi_item_velocity_x");
            tag.remove("ssi_item_velocity_y");
            tag.remove("ssi_item_velocity_z");
            return true;
        });
    }
}
