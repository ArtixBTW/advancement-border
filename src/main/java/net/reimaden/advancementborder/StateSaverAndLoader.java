package net.reimaden.advancementborder;

import java.util.ArrayList;
import java.util.HashSet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;

public final class StateSaverAndLoader extends SavedData {
    private static final String ADVANCEMENTS_KEY = "completedAdvancements";

    public final HashSet<Identifier> completedAdvancements;

    public StateSaverAndLoader() {
        this(new HashSet<>());
    }

    public StateSaverAndLoader(HashSet<Identifier> completedAdvancements) {
        this.completedAdvancements = completedAdvancements;
    }

    private static final Codec<StateSaverAndLoader> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    // https://docs.fabricmc.net/develop/codecs#serializing-and-deserializing
                    // https://docs.neoforged.net/docs/datastorage/codecs/
                    // https://docs.neoforged.net/docs/networking/streamcodecs/
                    Identifier.CODEC.listOf()
                            // make into a (mutable) set
                            .xmap(HashSet::new, ArrayList::new)
                            .fieldOf(ADVANCEMENTS_KEY)
                            .forGetter(state -> state.completedAdvancements))
                    .apply(instance, StateSaverAndLoader::new));

    private static final SavedDataType<StateSaverAndLoader> TYPE = new SavedDataType<>(
            AdvancementBorder.MOD_ID,
            StateSaverAndLoader::new,
            CODEC,
            null);

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        // TODO: check dimensions support in 1.21.9+
        ServerLevel level = server.overworld();
        assert level != null;

        DimensionDataStorage manager = level.getDataStorage();
        return manager.computeIfAbsent(TYPE);
    }
}
