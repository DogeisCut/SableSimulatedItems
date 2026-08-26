package io.github.dogeiscut.simulated_items.event;

import dev.ryanhcode.sable.api.event.SablePrePhysicsTickEvent;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.neoforge.event.ForgeSablePostPhysicsTickEvent;
import dev.ryanhcode.sable.neoforge.event.ForgeSablePrePhysicsTickEvent;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import io.github.dogeiscut.simulated_items.content.subLevelItem.SubLevelItemBlockEntity;
import io.github.dogeiscut.simulated_items.registry.SSIBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class ItemSubLevelEntityWatcher {

    @SubscribeEvent
    public static void onItemJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) return;
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) return;
        if (itemEntity.getPersistentData().hasUUID("ssi_sublevel")) return;

        // TODO: So, when spawning a sub level:
        // Apply random rotation and random angular velocity if the source item's linear velocity isn't (close to) 0
        // scale the angular velocity by source item linear velocity, the closer it is to 0, the close the angular velocity will be to 0

        // TODO: Hide original item and disable its collision
        // TODO: Attach item to sub level
        // TODO: make sub level not collide with player when first thrown out for a bit

        final ServerSubLevel assembledSubLevel;
        final BlockState itemBlock = SSIBlocks.SUB_LEVEL_ITEM.get().defaultBlockState();

        ServerLevel level = (ServerLevel) event.getLevel();
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        final Pose3d pose = new Pose3d();

        pose.position().set(itemEntity.getX() + 0.5, itemEntity.getY() + 0.5, itemEntity.getZ() + 0.5);

        assert container != null;
        assembledSubLevel = (ServerSubLevel) container.allocateNewSubLevel(pose);
        final LevelPlot plot = assembledSubLevel.getPlot();

        final ChunkPos center = plot.getCenterChunk();
        plot.newEmptyChunk(center);
        plot.getEmbeddedLevelAccessor().setBlock(BlockPos.ZERO, itemBlock, 3);

        if (plot.getEmbeddedLevelAccessor().getBlockEntity(BlockPos.ZERO) instanceof SubLevelItemBlockEntity be) {
            be.setItemStack(itemEntity.getItem());
        }

        final BlockPos plotAnchor = plot.getCenterBlock();
        final Vector3dc centerOfMass = assembledSubLevel.getMassTracker().getCenterOfMass();
        final Vector3d subLevelCenter = new Vector3d(itemEntity.getX() - 0.5,itemEntity.getY() - 0.5,itemEntity.getZ() - 0.5);

        if (centerOfMass != null) {
            subLevelCenter.add(centerOfMass.x() - plotAnchor.getX(), centerOfMass.y() - plotAnchor.getY(), centerOfMass.z() - plotAnchor.getZ());
        } else {
            assembledSubLevel.logicalPose().rotationPoint()
                    .set(plotAnchor.getX() + 0.5, plotAnchor.getY() + 0.5, plotAnchor.getZ() + 0.5);
        }

        assembledSubLevel.logicalPose().position().set(subLevelCenter.x, subLevelCenter.y, subLevelCenter.z);

        final SubLevelPhysicsSystem physicsSystem = container.physicsSystem();
        final PhysicsPipeline pipeline = physicsSystem.getPipeline();

        pipeline.teleport(assembledSubLevel, assembledSubLevel.logicalPose().position(), assembledSubLevel.logicalPose().orientation());
        assembledSubLevel.updateLastPose();

        itemEntity.getPersistentData().putUUID("ssi_sublevel", assembledSubLevel.getUniqueId());

        CompoundTag tag = assembledSubLevel.getUserDataTag();
        if (tag == null) {
            tag = new CompoundTag();
            assembledSubLevel.setUserDataTag(tag);
        }

        tag.putUUID("ssi_item", itemEntity.getUUID());
        // removed once the velocity is applied
        tag.putDouble("ssi_item_velocity_x", itemEntity.getDeltaMovement().x);
        tag.putDouble("ssi_item_velocity_y", itemEntity.getDeltaMovement().y);
        tag.putDouble("ssi_item_velocity_z", itemEntity.getDeltaMovement().z);
    }

    @SubscribeEvent
    public static void onItemLeave(EntityLeaveLevelEvent event) {
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

//    @SubscribeEvent
//    public void onSablePostPhysicsTickEvent(ForgeSablePostPhysicsTickEvent event) {
//      //TODO: apply item velocity
//    }
}