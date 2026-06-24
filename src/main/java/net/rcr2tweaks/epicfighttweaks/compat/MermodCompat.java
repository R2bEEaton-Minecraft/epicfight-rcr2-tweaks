package net.rcr2tweaks.epicfighttweaks.compat;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yesman.epicfight.api.forgeevent.BattleModeSustainableEvent;
import java.lang.reflect.Method;

public class MermodCompat {
    private static final Logger LOGGER = LogManager.getLogger("epicfighttweaks/mermod");
    private static final String MOD_ID = "mermod";

    // io.github.thatpreston.mermod.MermodClient.shouldRenderTail(Player) → boolean
    private static Method shouldRenderTail;
    private static boolean available = false;

    public static void register(IEventBus forgeEventBus) {
        if (!ModList.get().isLoaded(MOD_ID)) { LOGGER.debug("Mermod not present"); return; }
        try {
            Class<?> client = Class.forName("io.github.thatpreston.mermod.MermodClient");
            shouldRenderTail = client.getMethod("shouldRenderTail", net.minecraft.world.entity.player.Player.class);
            available = true;
            forgeEventBus.<BattleModeSustainableEvent>addListener(MermodCompat::onBattleModeSustainable);
            LOGGER.info("Mermod compat active");
        } catch (ClassNotFoundException e) {
            LOGGER.warn("Mermod detected but MermodClient not found — update MermodCompat.java");
        } catch (NoSuchMethodException e) {
            LOGGER.warn("Mermod detected but shouldRenderTail not found ({})", e.getMessage());
        } catch (Exception e) { LOGGER.error("Mermod compat failed", e); }
    }

    public static boolean shouldHideLeg(net.minecraft.world.entity.player.Player player) {
        if (!available) return false;
        try { return Boolean.TRUE.equals(shouldRenderTail.invoke(null, player)); }
        catch (Exception e) { return false; }
    }

    private static void onBattleModeSustainable(BattleModeSustainableEvent event) {
        if (!available) return;
        try {
            if (Boolean.TRUE.equals(shouldRenderTail.invoke(null, event.getPlayerPatch().getOriginal())))
                event.setCanceled(true);
        } catch (Exception ignored) {}
    }
}
