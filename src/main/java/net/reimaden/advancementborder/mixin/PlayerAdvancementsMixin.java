package net.reimaden.advancementborder.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.border.WorldBorder;
import net.reimaden.advancementborder.AdvancementBorder;
import net.reimaden.advancementborder.StateSaverAndLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
                                          @Share("announceChat") LocalBooleanRef booleanRef, @Share("increase") LocalDoubleRef doubleRef) {
        // A catch-all solution for "non-advancement" advancements
        // Excludes recipe, root, and technical advancements used by some data packs
        Optional<DisplayInfo> displayInfo = holder.value().display();
        if (displayInfo.isEmpty() || !displayInfo.get().shouldAnnounceChat()) {
            return;
        }

        Identifier advancement = holder.id();
        String[] advancements = AdvancementBorder.config.advancementWhitelist;
        // If the whitelist is not empty, check if the awarded advancement is in the whitelist
        if (advancements.length > 0) {
            if (!AdvancementBorder.config.invertList) {
                if (Arrays.stream(advancements).noneMatch(resourceLocation -> Identifier.parse(resourceLocation).equals(advancement))) {
                    return; // Return early since the awarded advancement is not whitelisted
                }
            } else {
                if (Arrays.stream(advancements).anyMatch(resourceLocation -> Identifier.parse(resourceLocation).equals(advancement))) {
                    return; // Return early since the awarded advancement is blacklisted
                }
            }
        }

        MinecraftServer server = this.player.level().getServer();
        if (server == null) return;

        UUID uuid = player.getUUID();

        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);

        Set<UUID> playersWithAdvancement = serverState.completedAdvancements.get(advancement);
        if (playersWithAdvancement == null) {
            serverState.completedAdvancements.put(advancement, new HashSet<>());
            playersWithAdvancement = serverState.completedAdvancements.get(advancement);
        }

        boolean shouldExpand = AdvancementBorder.config.perPlayerAdvancements
            && !playersWithAdvancement.contains(uuid)
            || playersWithAdvancement.size() == 0;

        if (!shouldExpand) return;

        boolean isAdvancementNewToPlayer = playersWithAdvancement.add(uuid);
        if (isAdvancementNewToPlayer) {
            serverState.setDirty();
        }

        double increase = switch (displayInfo.get().getType()) {
            case TASK -> AdvancementBorder.config.increaseAmountTask;
            case GOAL -> AdvancementBorder.config.increaseAmountGoal;
            case CHALLENGE -> AdvancementBorder.config.increaseAmountChallenge;
        };
        if (increase <= 0) return;

        // Setting the border in the Nether or the End doesn't work
        WorldBorder border = server.overworld().getWorldBorder();
        border.setSize(border.getSize() + increase);

        booleanRef.set(true);
        doubleRef.set(increase);
    }

    @Inject(
            method = "award",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void advancementborder$announceChat(AdvancementHolder holder, String criterion, CallbackInfoReturnable<Boolean> cir,
                                                @Share("announceChat") LocalBooleanRef booleanRef, @Share("increase") LocalDoubleRef doubleRef) {
        if (booleanRef.get()) {
            AdvancementBorder.sendExpansionNotification(this.playerList, doubleRef.get());
        }
    }
}
