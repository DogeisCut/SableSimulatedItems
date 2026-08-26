package io.github.dogeiscut.simulated_items.event;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import io.github.dogeiscut.simulated_items.SSI;
import io.github.dogeiscut.simulated_items.content.subLevelItem.SubLevelItemBlock;
import io.github.dogeiscut.simulated_items.content.subLevelItem.SubLevelItemBlockEntity;
import io.github.dogeiscut.simulated_items.registry.SSIBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class ItemSubLevelSpawner {

    @SubscribeEvent
    public static void onItemJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) return;
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) return;

        // TODO: So, when spawning a sub level:
        // Apply random rotation and random angular velocity if the source item's linear velocity isn't (close to) 0
        // scale the angular velocity by source item linear velocity, the closer it is to 0, the close the angular velocity will be to 0

        // TODO: Hide original item and disable its collision
        // TODO: Attach item to sub level
        // TODO: delete sub level on item destroy and vice versa
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

        final SubLevel containingSubLevel = Sable.HELPER.getContaining(itemEntity);
        if (containingSubLevel != null) {
            SubLevelAssemblyHelper.kickFromContainingSubLevel((ServerLevel) itemEntity.level(), physicsSystem, pipeline, assembledSubLevel, containingSubLevel);
            assembledSubLevel.logicalPose().orientation().set(containingSubLevel.logicalPose().orientation());
        }

        pipeline.teleport(assembledSubLevel, assembledSubLevel.logicalPose().position(), assembledSubLevel.logicalPose().orientation());
        assembledSubLevel.updateLastPose();
    }
}