package com.lfaoanl.marketcrates.common.blocks.states;

import net.minecraft.world.level.block.Block;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Arrays;
import java.util.Collection;

public enum CrateType implements StringRepresentable {

    DEFAULT("default", 8d, false),
    INCLINED("inclined", 14d, true),
    DOUBLE("double", 16d, true);

    private final String name;
    private final double height;
    private final boolean resourceName;

    /*
    shapes.put("default", Block.makeCuboidShape(2.0D, 0.0D, 0.0D, 14.0D, 8.0D, 16.0D));
    shapes.put("inclined", Block.makeCuboidShape(2.0D, 0.0D, 0.0D, 14.0D, 14.0D, 16.0D));
    shapes.put("default_horizontal", Block.makeCuboidShape(0.0D, 0.0D, 2.0D, 16.0D, 8.0D, 14.0D));
    shapes.put("inclined_horizontal", Block.makeCuboidShape(0.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D));
     */
    CrateType(String name, double height, boolean resourceName) {
        this.name = name;
        this.height = height;
        this.resourceName = resourceName;
    }

    public static Collection<CrateType> allValues() {
        return Arrays.asList(DEFAULT, INCLINED, DOUBLE);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public VoxelShape getShape(boolean horizontal) {
        if (horizontal) {
            return Block.box(0.0D, 0.0D, 2.0D, 16.0D, height, 14.0D);
        }
        return Block.box(2.0D, 0.0D, 0.0D, 14.0D, height, 16.0D);
    }

    public boolean isDouble() {
        return this == DOUBLE;
    }

    public CrateType opposite() {
        return this == DEFAULT ? INCLINED : DEFAULT;
    }

    /**
     * Used for forge data generation
     * @return
     */
    public String getResource() {

        return "crate" + (resourceName ? "_" + this.name : "");
    }

    public static CrateType[] all() {
        return new CrateType[]{DEFAULT, DOUBLE, INCLINED};
    }
}
