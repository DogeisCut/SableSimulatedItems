package io.github.dogeiscut.simulated_items.content.subLevelItem;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum SubLevelItemShape implements StringRepresentable {
    ITEM("item", Shapes.box(0.3125, 0.3125, 0.4375, 0.6875, 0.6875, 0.5625 )),
    BLOCK("block", Shapes.box(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875 ));

    private final String name;
    private final VoxelShape shape;

    private SubLevelItemShape(String name, VoxelShape shape) {
        this.name = name;
        this.shape = shape;
    }

    public String getSerializedName() {
        return this.name;
    }

    public VoxelShape getShape() {
        return this.shape;
    }
}
