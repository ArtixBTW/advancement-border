package net.reimaden.advancementborder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.minecraft.server.players.PlayerList;

public final class AdvancementBorder implements ModInitializer {
    public static final String MOD_ID = "advancementborder";
    public static final String MOD_NAME = "Advancement Border";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final AdvancementBorderConfig config = AdvancementBorderConfig.createAndLoad();

    @Override
    public void onInitialize() {
        // ConfigHolder<AdvancementBorderConfig> configHolder = Configuration.registerConfig(AdvancementBorderConfig.class, ConfigFormats.YAML);
        // config = configHolder.getConfigInstance();
    }

    public static void sendExpansionNotification(PlayerList playerList, Object... translatableArgs) {
        // switch (config.notificationStyle) {
        //     case CHAT, ACTION_BAR -> {
        //         String translationKey = AdvancementBorder.config.detailedNotifications
        //                 ? ".expand_detailed"
        //                 : ".expand_basic";
        //
        //         int color = Integer.parseInt(config.notificationColor.substring(1), 16);
        //         playerList.broadcastSystemMessage(
        //                 Component.translatable(AdvancementBorder.MOD_ID + translationKey, translatableArgs).withColor(color),
        //                 config.notificationStyle.equals(AdvancementBorderConfig.NotificationStyle.ACTION_BAR)
        //         );
        //     }
        //     case NONE -> {}
        // }
    }
}
