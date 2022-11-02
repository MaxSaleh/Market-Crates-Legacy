package com.lfaoanl.marketcrates.common.gui;

import com.lfaoanl.marketcrates.common.Ref;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CrateScreen extends BaseCrateScreen<BaseCrateContainer> {

    private ResourceLocation GUI = new ResourceLocation(Ref.MODID, "textures/gui/crate.png");

    public CrateScreen(BaseCrateContainer container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    ResourceLocation getGuiTexture() {
        return GUI;
    }
}