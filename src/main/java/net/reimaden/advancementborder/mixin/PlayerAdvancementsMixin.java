package net.reimaden.advancementborder.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.reimaden.advancementborder.AdvancementBorder;
import net.reimaden.advancementborder.AdvancementBorderConfig.AdvancementsConfig.BlacklistConfig;
import net.reimaden.advancementborder.AdvancementBorderConfig.DimensionConfigs.DimensionConfig;
import net.reimaden.advancementborder.StateSaverAndLoader;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {
    @Shadow private ServerPlayer player;
    @Shadow @Final private PlayerList playerList;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
            method = "award",
            at = @At(
                    value = "INVOKE:FIRST",
                    target = "Lnet/minecraft/advancements/AdvancementHolder;value()Lnet/minecraft/advancements/Advancement;"
            )
    )
    private void advancementborder$expand(AdvancementHolder holder, String criterion, CallbackInfoReturnable<Boolean> cir,
                                          @Share("announceChat") LocalBooleanRef announceChat, @Share("increase") LocalDoubleRef doubleRef) {
        // A catch-all solution for "non-advancement" advancements
        // Excludes recipe, root, and technical advancements used by some data packs
        Optional<DisplayInfo> displayInfo = holder.value().display();
        if (displayInfo.isEmpty() || !displayInfo.get().shouldAnnounceChat()) {
            return;
        }

        Identifier advancement = holder.id();

        BlacklistConfig blacklist = AdvancementBorder.config.advancements.blacklist;
        if (!blacklist.advancements.isEmpty()) {
            boolean dontExpand = blacklist.advancements.contains(advancement);
            if (blacklist.invert) {
                dontExpand = !dontExpand;
            }
            if (dontExpand) return;
        }

        MinecraftServer server = this.player.level().getServer();
        assert server != null;

        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);

        boolean isNewAdvancement = serverState.completedAdvancements.add(advancement);
        if (isNewAdvancement) serverState.setDirty();

        // If perPlayerAdvancements then any advancement being awarded is new
        boolean shouldExpand = AdvancementBorder.config.advancements.perPlayer || isNewAdvancement;

        if (!shouldExpand) return;

        server.registries().compositeAccess().lookupOrThrow(Registries.DIMENSION).forEach(level -> {
            ResourceKey<Level> dimension = level.dimension();
            Identifier id = dimension.identifier();

            DimensionConfig config = AdvancementBorder.config.dimensions.dimensions.getOrDefault(
                    id,
                    AdvancementBorder.config.dimensions.fallback);

            double increase = switch (displayInfo.get().getType()) {
                case TASK -> config.increaseAmount.task;
                case GOAL -> config.increaseAmount.goal;
                case CHALLENGE -> config.increaseAmount.challenge;
            };

            WorldBorder border =  level.getWorldBorder();

            // Ensure the world border doesn't go too small, crash the game, and break the world
            border.setSize(Math.max(1.0, border.getSize() + increase));
        });

        announceChat.set(true);
        // doubleRef.set(increase);
    }

    // @Inject(
    //         method = "award",
    //         at = @At(
    //                 value = "INVOKE",
    //                 target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V",
    //                 shift = At.Shift.AFTER
    //         )
    // )
    // private void advancementborder$announceChat(AdvancementHolder holder, String criterion, CallbackInfoReturnable<Boolean> cir,
    //                                             @Share("announceChat") LocalBooleanRef announceChat, @Share("increase") LocalDoubleRef doubleRef) {
    //     if (announceChat.get()) {
    //         AdvancementBorder.sendExpansionNotification(this.playerList, doubleRef.get());
    //     }
    // }
}
