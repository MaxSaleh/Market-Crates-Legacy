package com.lfaoanl.marketcrates.common.blocks.states;

import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.Collection;

public class CrateTypeProperty extends EnumProperty<CrateType> {

    protected CrateTypeProperty(String name, Class<CrateType> valueClass, Collection<CrateType> allowedValues) {
        super(name, valueClass, allowedValues);
    }

    public static CrateTypeProperty create(String type) {
        return new CrateTypeProperty(type, CrateType.class, CrateType.allValues());
    }
}
