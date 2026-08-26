package io.github.dogeiscut.simulated_items.content.subLevelItem;

import dev.ryanhcode.sable.api.block.BlockSubLevelCollisionShape;
import io.github.dogeiscut.simulated_items.registry.SSIBlockEntities;
import io.github.dogeiscut.simulated_items.registry.SSIBlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

// TODO: prevent placing blocks off of this block
// TODO: fix particles with sublevels
// TODO: tag with very light. or just like, create custom physics config for it

public class SubLevelItemBlock extends Block implements EntityBlock, BlockSubLevelCollisionShape {

    public static final Property<SubLevelItemShape> SHAPE = SSIBlockStateProperties.SHAPE;

    public SubLevelItemBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(getStateDefinition().any()
                .setValue(SHAPE, SubLevelItemShape.ITEM));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof SubLevelItemBlockEntity be) {
            return be.getItemStack().copy();
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof SubLevelItemBlockEntity be) {
            be.setItemStack(be.getItemStack());
        }
    }

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
        return state.getValue(SHAPE).getShape();
        //return Shapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(SHAPE).getShape();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SubLevelItemBlockEntity(SSIBlockEntities.SUB_LEVEL_ITEM.get(), pos, state);
    }

    @Override
    public VoxelShape getSubLevelCollisionShape(BlockGetter blockGetter, BlockState state) {
        return state.getValue(SHAPE).getShape();
    }
}
