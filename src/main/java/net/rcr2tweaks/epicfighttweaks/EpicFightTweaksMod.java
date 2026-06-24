package net.rcr2tweaks.epicfighttweaks;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.common.MinecraftForge;
import net.rcr2tweaks.epicfighttweaks.compat.MermodCompat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(EpicFightTweaksMod.MOD_ID)
public class EpicFightTweaksMod {
    public static final String MOD_ID = "epicfighttweaks";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public EpicFightTweaksMod() {
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        MermodCompat.register(forgeEventBus);
        LOGGER.info("Epic Fight RCR2 Tweaks loaded.");
    }
}
