package net.reimaden.advancementborder;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.util.Walkable;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedIdentifierMap;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedAny;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedColor;
import net.minecraft.resources.Identifier;

@Version(version = 0)
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

    @Comment("Configuration of how to respond to advancements")
    public AdvancementsConfig advancements = new AdvancementsConfig();

    public static class AdvancementsConfig implements Walkable {
        @Comment("Whether each player contributes separately instead of using a global list of advancements")
        public boolean perPlayer = false;

        @Comment("Advancements that should not expand the world border")
        public BlacklistConfig blacklist = new BlacklistConfig();

        public static class BlacklistConfig implements Walkable {
            @Comment("Make the blacklist act as a whitelist, allowing only the advancements in it to expand the world border")
            public boolean invert = false;

            @Comment("The advancements in the blacklist")
            public Set<Identifier> advancements = new HashSet<>();
        }
    }

    @Comment("Expansion Notification Configuration")
    public ExpansionNotificationConfig expansionNotification = new ExpansionNotificationConfig();

    public static enum NotificationLocation {
        CHAT,
        ACTION_BAR,
        DISABLED,
    }

    public static class ExpansionNotificationConfig implements Walkable {
        @Comment("Where to send notifications to")
        public NotificationLocation location = NotificationLocation.CHAT;

        @Comment("Whether to display the amount of blocks the world border expands in each notification")
        public boolean showAmountOfBlocks = false;

        @Comment("Text color of notifications")
        public ValidatedColor textColor = new ValidatedColor(Color.decode("#55FFFF"), false);
    }

    @Comment("Per-Dimension Configuration")
    public DimensionConfigs dimensions = new DimensionConfigs();

    public static class DimensionConfigs implements Walkable {
        @Comment("The config to use if a dimension is not explicitly configured")
        public DimensionConfig fallback = new DimensionConfig();

        @Comment("Explicitly configured dimensions")
        public ValidatedIdentifierMap<DimensionConfig> configured = new ValidatedIdentifierMap.Builder<DimensionConfig>()
                .keyHandler(new ValidatedIdentifier(Identifier.withDefaultNamespace("overworld")))
                .valueHandler(new ValidatedAny<DimensionConfig>(new DimensionConfig()))
                .build();

        public static class DimensionConfig implements Walkable {
            @Comment("How much to increase the world border in this dimension")
            public IncreaseAmountConfig increaseAmount = new IncreaseAmountConfig();
        }

        public static class IncreaseAmountConfig implements Walkable {
            @Comment("How much the world border should expand with each task advancement")
            public double task = 2.0;

            @Comment("How much the world border should expand with each goal advancement")
            public double goal = 2.0;

            @Comment("How much the world border should expand with each challenge advancement")
            public double challenge = 2.0;
        }
    }
}
