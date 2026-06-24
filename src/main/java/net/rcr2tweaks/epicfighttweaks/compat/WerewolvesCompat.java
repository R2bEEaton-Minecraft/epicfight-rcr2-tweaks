package net.rcr2tweaks.epicfighttweaks.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yesman.epicfight.api.client.forgeevent.RenderEpicFightPlayerEvent;
import yesman.epicfight.api.forgeevent.BattleModeSustainableEvent;
import java.lang.reflect.Method;

// NOTE: Werewolf forms are in the Werewolves mod, not Vampirism.
public class WerewolvesCompat {
    private static final Logger LOGGER = LogManager.getLogger("epicfighttweaks/werewolves");
    private static final String MOD_ID = "werewolves";

    private static Method werewolfGet;
    private static Method getForm;
    private static Method isTransformed;
    // null = not yet attempted; true = ready; false = failed
    private static Boolean initialized = null;

    public static void register(IEventBus forgeEventBus) {
        if (!ModList.get().isLoaded(MOD_ID)) { LOGGER.debug("Werewolves not present"); return; }
        // Subscribe now, but defer Class.forName until first use — WerewolfPlayer's static
        // initializer touches Vampirism registries that aren't populated yet during mod construction.
        forgeEventBus.<BattleModeSustainableEvent>addListener(WerewolvesCompat::onBattleModeSustainable);
        forgeEventBus.<RenderEpicFightPlayerEvent>addListener(WerewolvesCompat::onRenderEpicFightPlayer);
        LOGGER.info("Werewolves form compat registered (lazy init)");
    }

    private static boolean lazyInit() {
        if (initialized != null) return initialized;
        try {
            Class<?> werewolfPlayerClass = Class.forName("de.teamlapen.werewolves.entities.player.werewolf.WerewolfPlayer");
            werewolfGet = werewolfPlayerClass.getMethod("get", Player.class);

            Class<?> werewolfFormClass = Class.forName("de.teamlapen.werewolves.api.entities.werewolf.WerewolfForm");
            isTransformed = werewolfFormClass.getMethod("isTransformed");

            Class<?> dataholderClass = Class.forName("de.teamlapen.werewolves.api.entities.werewolf.IWerewolfDataholder");
            getForm = dataholderClass.getMethod("getForm");

            initialized = true;
            LOGGER.info("Werewolves form compat active");
        } catch (Throwable e) {
            initialized = false;
            LOGGER.error("Werewolves compat init failed: {}", e.getMessage());
        }
        return initialized;
    }

    private static boolean isInWerewolfForm(Player player) {
        if (!lazyInit()) return false;
        try {
            Object werewolfPlayer = werewolfGet.invoke(null, player);
            if (werewolfPlayer == null) return false;
            Object form = getForm.invoke(werewolfPlayer);
            return Boolean.TRUE.equals(isTransformed.invoke(form));
        } catch (Throwable e) { return false; }
    }

    private static void onBattleModeSustainable(BattleModeSustainableEvent event) {
        if (isInWerewolfForm(event.getPlayerPatch().getOriginal())) event.setCanceled(true);
    }

    private static void onRenderEpicFightPlayer(RenderEpicFightPlayerEvent event) {
        if (isInWerewolfForm(event.getPlayerPatch().getOriginal())) event.setShouldRender(false);
    }
}
