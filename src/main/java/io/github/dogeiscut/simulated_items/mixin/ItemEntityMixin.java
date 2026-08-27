package io.github.dogeiscut.simulated_items.mixin;

import io.github.dogeiscut.simulated_items.client.PickupItemAccess;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin implements PickupItemAccess {

    @Unique
    private boolean simulated_items$isPickupItem = false;

    @Override
    public boolean simulated_items$isPickupItem() {
        return simulated_items$isPickupItem;
    }

    @Override
    public void simulated_items$setPickupItem(boolean isPickupItem) {
        this.simulated_items$isPickupItem = isPickupItem;
    }
}