package net.rcr2tweaks.epicfighttweaks.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yesman.epicfight.api.client.forgeevent.RenderEpicFightPlayerEvent;
import yesman.epicfight.api.forgeevent.BattleModeSustainableEvent;
import java.lang.reflect.Method;

// NOTE: Bat form is in Vampirism, not Werewolves.
public class VampirismCompat {
    private static final Logger LOGGER = LogManager.getLogger("epicfighttweaks/vampirism");
    private static final String MOD_ID = "vampirism";
    private static final ResourceLocation BAT_ACTION_ID = ResourceLocation.tryParse("vampirism:bat");

    private static Method vampireGet;
    private static Method getActionHandler;
    private static Method isActionActiveRL;
    // null = not yet attempted; true = ready; false = failed
    private static Boolean initialized = null;

    public static void register(IEventBus forgeEventBus) {
        if (!ModList.get().isLoaded(MOD_ID)) { LOGGER.debug("Vampirism not present"); return; }
        // Subscribe now, defer Class.forName until first use to avoid triggering static
        // initializers before Vampirism's registries are populated.
        forgeEventBus.<BattleModeSustainableEvent>addListener(VampirismCompat::onBattleModeSustainable);
        forgeEventBus.<RenderEpicFightPlayerEvent>addListener(VampirismCompat::onRenderEpicFightPlayer);
        LOGGER.info("Vampirism bat-form compat registered (lazy init)");
    }

    private static boolean lazyInit() {
        if (initialized != null) return initialized;
        try {
            Class<?> vampirePlayerClass = Class.forName("de.teamlapen.vampirism.entity.player.vampire.VampirePlayer");
            vampireGet = vampirePlayerClass.getMethod("get", Player.class);

            Class<?> iVampirePlayerClass = Class.forName("de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer");
            getActionHandler = iVampirePlayerClass.getMethod("getActionHandler");

            Class<?> iActionHandlerClass = Class.forName("de.teamlapen.vampirism.api.entity.player.actions.IActionHandler");
            isActionActiveRL = iActionHandlerClass.getMethod("isActionActive", ResourceLocation.class);

            initialized = true;
            LOGGER.info("Vampirism bat-form compat active");
        } catch (Throwable e) {
            initialized = false;
            LOGGER.error("Vampirism compat init failed: {}", e.getMessage());
        }
        return initialized;
    }

    private static boolean isInBatForm(Player player) {
        if (!lazyInit()) return false;
        try {
            Object vampPlayer = vampireGet.invoke(null, player);
            if (vampPlayer == null) return false;
            Object handler = getActionHandler.invoke(vampPlayer);
            return Boolean.TRUE.equals(isActionActiveRL.invoke(handler, BAT_ACTION_ID));
        } catch (Throwable e) { return false; }
    }

    private static void onBattleModeSustainable(BattleModeSustainableEvent event) {
        if (isInBatForm(event.getPlayerPatch().getOriginal())) event.setCanceled(true);
    }

    private static void onRenderEpicFightPlayer(RenderEpicFightPlayerEvent event) {
        if (isInBatForm(event.getPlayerPatch().getOriginal())) event.setShouldRender(false);
    }
}
