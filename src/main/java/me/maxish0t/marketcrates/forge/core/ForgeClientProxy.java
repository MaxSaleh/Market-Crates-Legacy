package me.maxish0t.marketcrates.forge.core;

import me.maxish0t.marketcrates.common.ClientProxy;
import me.maxish0t.marketcrates.common.gui.CrateDoubleScreen;
import me.maxish0t.marketcrates.common.gui.CrateScreen;
import me.maxish0t.marketcrates.common.render.CrateBlockEntityRenderer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ForgeClientProxy extends ClientProxy {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void init(final FMLClientSetupEvent event) {

        MenuScreens.register(CrateRegistry.CONTAINER_CRATE.get(), CrateScreen::new);
        MenuScreens.register(CrateRegistry.CONTAINER_CRATE_DOUBLE.get(), CrateDoubleScreen::new);
    }

    public void registerBlockEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(CrateRegistry.CRATE_TILE.get(), CrateBlockEntityRenderer::new);
    }
}
