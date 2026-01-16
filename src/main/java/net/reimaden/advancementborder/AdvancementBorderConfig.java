package net.reimaden.advancementborder;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.util.Walkable;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedIdentifierMap;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedAny;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedColor;
import net.minecraft.resources.Identifier;

public final class AdvancementBorderConfig extends Config {
    private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath(
            AdvancementBorder.MOD_ID,
            "server");

    public AdvancementBorderConfig() {
        super(IDENTIFIER);
    }

    @Override
    public FileType fileType() {
        return FileType.JSON5;
    }

    @Override
    public SaveType saveType() {
        return SaveType.SEPARATE;
    }

    public AdvancementsConfig advancements = new AdvancementsConfig();

    public static class AdvancementsConfig implements Walkable {
        /**
         * Whether each player contributes separately
         * instead of using a global list of advancements
         */
        public boolean perPlayer = false;

        public BlacklistConfig blacklist = new BlacklistConfig();

        public static class BlacklistConfig implements Walkable {
            public boolean invert = false;

            public Set<Identifier> advancements = new HashSet<>();
        }
    }

    public ExpansionNotificationConfig expansionNotification = new ExpansionNotificationConfig();

    public static enum NotificationLocation {
        CHAT,
        ACTION_BAR,
        NONE,
    }

    public static class ExpansionNotificationConfig implements Walkable {
        /** Whether to display the amount of blocks the world border expands */
        public boolean showAmountOfBlocks = false;

        /** Where to send notifications to */
        public NotificationLocation location = NotificationLocation.CHAT;

        /** Text color of world border notifications */
        public ValidatedColor textColor = new ValidatedColor(Color.decode("#55FFFF"), false);
    }

    public DimensionConfigs dimensions = new DimensionConfigs();

    public static class DimensionConfigs implements Walkable {
        public DimensionConfig fallback = new DimensionConfig();

        public ValidatedIdentifierMap<DimensionConfig> dimensions = new ValidatedIdentifierMap.Builder<DimensionConfig>()
                .keyHandler(new ValidatedIdentifier(Identifier.withDefaultNamespace("overworld")))
                .valueHandler(new ValidatedAny<DimensionConfig>(new DimensionConfig()))
                .build();

        public static class DimensionConfig implements Walkable {
            public CenterConfig center = new CenterConfig();
            public IncreaseAmountConfig increaseAmount = new IncreaseAmountConfig();

            public static class CenterConfig implements Walkable {
                public double x = 0.5;
                public double z = 0.5;
            }

            public static class IncreaseAmountConfig implements Walkable {
                public double task = 2.0;
                public double goal = 2.0;
                public double challenge = 2.0;
            }
        }
    }
}
