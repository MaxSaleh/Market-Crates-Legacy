package com.lfaoanl.marketcrates.forge.network;

import com.lfaoanl.marketcrates.common.MarketCrates;
import com.lfaoanl.marketcrates.common.Ref;
import com.lfaoanl.marketcrates.forge.blocks.CrateBlockEntity;
import com.lfaoanl.marketcrates.forge.network.packets.CrateItemsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class CratesPacketHandler {


    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Ref.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init(FMLCommonSetupEvent event) {
        INSTANCE.registerMessage(0, CrateItemsPacket.class, CrateItemsPacket::encode, CrateItemsPacket::decode, CratesPacketHandler::handle);
    }

    public static void handle(final CrateItemsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            Level world = MarketCrates.proxy.getWorld();

            if (world != null) {
                BlockEntity tile = world.getBlockEntity(msg.getPosition());

                if (tile instanceof CrateBlockEntity) {
                    ((CrateBlockEntity) tile).receiveContents(msg.items);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }


}
