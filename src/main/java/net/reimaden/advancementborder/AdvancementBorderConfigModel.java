package net.reimaden.advancementborder;

import java.util.ArrayList;
import java.util.List;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Nest;
import io.wispforest.owo.config.annotation.PredicateConstraint;
import io.wispforest.owo.ui.core.Color;
import net.minecraft.ChatFormatting;

@Modmenu(modId = AdvancementBorder.MOD_ID)
@Config(name = "advancement-border", wrapperName = "AdvancementBorderConfig")
public final class AdvancementBorderConfigModel {
    public static final class RegexPatterns {
        /**
          * Matches to see if the `String` is in the identifer format.
          * i.e "minecraft:overworld", "epic_mod:epic_advancement", "*:*"
          */
        public static final String isIdentifier = "^[a-z0-9_\\-.]+:[a-z0-9_\\-./]+$";
    }

    @Nest
    public AdvancementsConfig advancements = new AdvancementsConfig();

    public static class AdvancementsConfig {
        /** Whether each player contributes separately instead of using a global list of advancements */
        public boolean perPlayer = false;

        @Nest
        public BlacklistConfig blacklist = new BlacklistConfig();

        public static class BlacklistConfig {
            public boolean invert = false;

            @PredicateConstraint("isAllIdentifiers")
            public List<String> advancements = new ArrayList<>();

            public static boolean isAllIdentifiers(List<String> advancements) {
                return advancements.stream().allMatch(s -> s.matches(RegexPatterns.isIdentifier));
            }
        }
    }

    @Nest
    public ExpansionNotificationConfig expansionNotification = new ExpansionNotificationConfig();

    public static enum NotificationLocation {
        CHAT,
        ACTION_BAR,
        NONE,
    }

    public static class ExpansionNotificationConfig {
        /** Whether to display the amount of blocks the world border expands */
        public boolean showAmountOfBlocks = false;

        /** Where to send notifications to */
        public NotificationLocation location = NotificationLocation.CHAT;

        /** Text color of world border notifications */
        public Color textColor = Color.ofFormatting(ChatFormatting.AQUA);
    }

    // @Nest
    // public DimensionsConfig dimensions = new DimensionsConfig();
    // public EdmMap dimensions = 
    // public EdmElement<Map<String, EdmElement<DimensionConfig>>> dimension = EdmElement.map(new HashMap<>());

    public static class DimensionConfig {
        public double x = 0.5;
    //     public Map<String, DimensionsConfig> dimensions = new HashMap<>();
    //
    //     public static class DimensionConfig {
    //         public double x = 0.5;
    //         public double y = 0.5;
    //     }
    }
}
