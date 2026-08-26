package io.github.dogeiscut.simulated_items.registry;

import io.github.dogeiscut.simulated_items.content.subLevelItem.SubLevelItemShape;
import net.minecraft.world.level.block.state.properties.*;

public class SSIBlockStateProperties {
    public static final EnumProperty<SubLevelItemShape> SHAPE;

    static {
        SHAPE = EnumProperty.create("shape", SubLevelItemShape.class);
    }
}
