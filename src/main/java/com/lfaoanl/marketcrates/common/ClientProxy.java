package com.lfaoanl.marketcrates.common;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

public class ClientProxy implements IMarketCratesProxy {

    @Override
    public Level getWorld() {
        return Minecraft.getInstance().level;
    }
}
