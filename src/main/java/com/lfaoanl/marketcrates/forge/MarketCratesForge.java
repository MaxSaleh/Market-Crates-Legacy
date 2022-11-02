package com.lfaoanl.marketcrates.forge;

import com.lfaoanl.marketcrates.common.CommonProxy;
import com.lfaoanl.marketcrates.common.MarketCrates;
import com.lfaoanl.marketcrates.common.Ref;
import com.lfaoanl.marketcrates.forge.core.CrateRegistry;
import com.lfaoanl.marketcrates.forge.core.ForgeClientProxy;
import com.lfaoanl.marketcrates.forge.data.CrateDataGenerator;
import com.lfaoanl.marketcrates.forge.network.CratesPacketHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Ref.MODID)
public class MarketCratesForge {

    public MarketCratesForge() {

        MarketCrates.proxy = DistExecutor.runForDist(() -> ForgeClientProxy::new, () -> CommonProxy::new);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MarketCrates.LOGGER.debug("Init CrateRegistry");
        CrateRegistry.init();
        MarketCrates.LOGGER.debug("Done initialising CrateRegistry");

        MarketCrates.LOGGER.debug("ClientOnly registry");
        DistExecutor.runWhenOn(Dist.CLIENT, MarketCratesForge::clientInit);
        modEventBus.addListener(this::entityRenderersEvent);
        MarketCrates.LOGGER.debug("Done! - ClientOnly registry");

        MarketCrates.LOGGER.debug("Init CratePacketHandler");
        modEventBus.addListener(CratesPacketHandler::init);
        MarketCrates.LOGGER.debug("Done! - Init CratePacketHandler");

        MarketCrates.LOGGER.debug("Init CrateDataGenerator");
        modEventBus.addListener(CrateDataGenerator::init);
        MarketCrates.LOGGER.debug("Init CrateDataGenerator");
    }

    private static Runnable clientInit() {
        return () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(ForgeClientProxy::init);
    }

    private void entityRenderersEvent(EntityRenderersEvent.RegisterRenderers event) {
        ((ForgeClientProxy) MarketCrates.proxy).registerBlockEntityRenderer(event);
    }
}
