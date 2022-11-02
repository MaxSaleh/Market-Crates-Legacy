package com.lfaoanl.marketcrates.common.gui;

import com.lfaoanl.marketcrates.common.Ref;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

public class CrateDoubleScreen extends BaseCrateScreen<BaseCrateContainer> {

    private ResourceLocation GUI = new ResourceLocation(Ref.MODID, "textures/gui/crate_double.png");

    public CrateDoubleScreen(BaseCrateContainer container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    ResourceLocation getGuiTexture() {
        return GUI;
    }
}