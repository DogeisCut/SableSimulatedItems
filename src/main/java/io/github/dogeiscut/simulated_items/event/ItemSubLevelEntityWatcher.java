package io.github.dogeiscut.simulated_items.event;

import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.neoforge.event.ForgeSablePrePhysicsTickEvent;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import io.github.dogeiscut.simulated_items.SSI;
import io.github.dogeiscut.simulated_items.content.subLevelItem.SubLevelItemBlock;
import io.github.dogeiscut.simulated_items.content.subLevelItem.SubLevelItemBlockEntity;
import io.github.dogeiscut.simulated_items.registry.SSIBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ItemSubLevelEntityWatcher {

    // TODO: maybe split up these events

    // TODO: leaky and is lost on save+quit, find better solution
    private final Set<UUID> pendingVelocity = new HashSet<>();

    private static final double EPSILON = 0.02;

    @SubscribeEvent
    public void onItemJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) return;
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) return;
        if (itemEntity.getPersistentData().hasUUID("ssi_sublevel")) return;

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

        pendingVelocity.add(assembledSubLevel.getUniqueId());
        tag.putDouble("ssi_item_velocity_x", itemEntity.getDeltaMovement().x);
        tag.putDouble("ssi_item_velocity_y", itemEntity.getDeltaMovement().y);
        tag.putDouble("ssi_item_velocity_z", itemEntity.getDeltaMovement().z);
    }

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

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().getBlockState(event.getPos()).getBlock() instanceof SubLevelItemBlock
                && event.getItemStack().getItem() instanceof BlockItem) {
            event.setCanceled(true);
        }
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