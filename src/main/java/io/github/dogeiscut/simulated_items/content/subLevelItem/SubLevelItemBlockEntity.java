package io.github.dogeiscut.simulated_items.content.subLevelItem;

import io.github.dogeiscut.simulated_items.config.PhysicsItemPropertiesLoader;
import io.github.dogeiscut.simulated_items.registry.SSIBlockEntities;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SubLevelItemBlockEntity extends BlockEntity {
    private ItemStack itemStack = ItemStack.EMPTY;

    public SubLevelItemBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public SubLevelItemBlockEntity(BlockPos pos, BlockState blockState) {
        this(SSIBlockEntities.SUB_LEVEL_ITEM.get(), pos, blockState);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }


    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack == null ? ItemStack.EMPTY : itemStack;
        setChanged();
        if (level != null && !level.isClientSide) {
            SubLevelItemShape newShape = PhysicsItemPropertiesLoader.resolveShape(this.itemStack)
                    .orElseGet(() -> (this.itemStack.getItem() instanceof BlockItem)
                            ? SubLevelItemShape.BLOCK
                            : SubLevelItemShape.ITEM);

            BlockState currentState = getBlockState();
            level.setBlock(worldPosition, currentState.setValue(SubLevelItemBlock.SHAPE, newShape), 3);
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!itemStack.isEmpty()) {
            tag.put("Item", itemStack.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Item", CompoundTag.TAG_COMPOUND)) {
            this.setItemStack(ItemStack.parse(registries, tag.getCompound("Item")).orElse(ItemStack.EMPTY));
        } else {
            this.setItemStack(ItemStack.EMPTY);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        super.onDataPacket(net, pkt, registries);
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            this.loadAdditional(tag, registries);
        }
    }

    public TextureAtlasSprite getParticleSprite() {
        return null;
    }
}