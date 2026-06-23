package com.minecartsextra;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(MinecartsExtra.MOD_ID)
public class MinecartsExtra {
    public static final String MOD_ID = "minecartsextra";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MinecartsExtra() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        com.minecartsextra.registry.ModEntities.register(modEventBus);
        com.minecartsextra.registry.ModItems.register(modEventBus);
        com.minecartsextra.network.ModMessages.register();

        modEventBus.addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM PREINIT");
    }
}
