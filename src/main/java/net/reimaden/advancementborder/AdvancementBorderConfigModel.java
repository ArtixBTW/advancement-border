package net.reimaden.advancementborder;

import java.util.ArrayList;
import java.util.List;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.PredicateConstraint;
import io.wispforest.owo.config.annotation.RegexConstraint;

@Modmenu(modId = AdvancementBorder.MOD_ID)
@Config(name = "advancement-border", wrapperName = "AdvancementBorderConfig")
public final class AdvancementBorderConfigModel {
    // Whether to display the amount of blocks the world border expands
    public boolean detailedNotifications = false;

    public static enum NotificationStyle {
        CHAT,
        ACTION_BAR,
        NONE,
    }

    // Whether to send world border notifications to the chat, action bar, or not at all
    public NotificationStyle notificationStyle = NotificationStyle.CHAT;

    // Color of world border notifications in hexadecimal
    // notificationColor: #55FFFF

    // Whether each player contributes separately instead of using a global list of advancements
    public boolean perPlayerAdvancements = true;

    private static final String isIdentifierRegex = "^[a-z0-9_\\-.]+:[a-z0-9_\\-./]+$,";

    // @RegexConstraint("^[a-z0-9_\\-.]+:[a-z0-9_\\-./]+$,")

    @PredicateConstraint("isAllIdentifiers")
    public List<String> blacklist = new ArrayList<>();

    public static boolean isAllIdentifiers(List<String> blacklist) {
        return blacklist.stream().allMatch(isIdentifierRegex::matches);
    }

    // @RegexConstraint("^[a-z0-9_\\-.]+:[a-z0-9_\\-./]+$,")
    // blacklist:
    // invert: false
    // elements: []
}
