package io.github.dogeiscut.simulated_items.content.subLevelItem;

import io.github.dogeiscut.simulated_items.registry.SSIBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.StructureVoidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SubLevelItemBlock extends Block implements EntityBlock {

    // TODO: make per-item/tag configurable via data once the item registry side exists
    private static final VoxelShape SHAPE = Shapes.box(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875 );
    private static final VoxelShape ITEM_SHAPE = Shapes.box(0.3125, 0.3125, 0.4375, 0.6875, 0.6875, 0.5625 );

    public SubLevelItemBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof SubLevelItemBlockEntity be) {
            return be.getItemStack().copy();
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    // TODO: make particles use item particles
    // TODO: prevent placing blocks off of

    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    public static Properties defaultProperties() {
        return Properties.of()
                .mapColor(MapColor.NONE)
                .noCollission()
                .noOcclusion()
                .strength(-1)
                .pushReaction(PushReaction.BLOCK)
                .isValidSpawn((state, level, pos, type) -> false)
                .isRedstoneConductor((state, level, pos) -> false)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (level.getBlockEntity(pos) instanceof SubLevelItemBlockEntity be) {
            if (be.getItemStack().getItem() instanceof BlockItem) {
                return SHAPE;
            }
        }
        return ITEM_SHAPE;
    }

    // TODO: Sable uses the wrong collision shape for full blocks. No idea how to fix this.
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (level.getBlockEntity(pos) instanceof SubLevelItemBlockEntity be) {
            if (be.getItemStack().getItem() instanceof BlockItem) {
                return SHAPE;
            }
        }
        return ITEM_SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SubLevelItemBlockEntity(SSIBlockEntities.SUB_LEVEL_ITEM.get(), pos, state);
    }
}
