package net.reimaden.advancementborder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedColor;
import net.fabricmc.api.ModInitializer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;

public final class AdvancementBorder implements ModInitializer {
    public static final String MOD_ID = "advancementborder";
    public static final String MOD_NAME = "Advancement Border";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final AdvancementBorderConfig config = ConfigApiJava.registerAndLoadConfig(
            AdvancementBorderConfig::new,
            // TODO: make sure we don't override clients
            RegisterType.BOTH);

    @Override
    public void onInitialize() {
    }

    public static void sendExpansionNotification(PlayerList playerList, String translationKey, ValidatedColor textColor, Object... translatableArgs) {
        switch (config.expansionNotification.location) {
            case CHAT, ACTION_BAR -> {
                playerList.broadcastSystemMessage(
                        Component.translatable(
                            AdvancementBorder.MOD_ID + translationKey,
                            translatableArgs).withColor(textColor.get().argb()),
                        config.expansionNotification.location.equals(AdvancementBorderConfig.NotificationLocation.ACTION_BAR));
            }
            case DISABLED -> {}
        }
    }
}
