package net.reimaden.advancementborder;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;

public final class StateSaverAndLoader extends SavedData {
    private static final String ADVANCEMENTS_KEY = "completedAdvancements";
    private static final String FRESH_WORLD_KEY = "isFreshWorld";

    public final HashMap<Identifier, Set<UUID>> completedAdvancements;
    public boolean isFreshWorld;

    public StateSaverAndLoader() {
        this(new HashMap<>(), true);
    }

    public StateSaverAndLoader(HashMap<Identifier, Set<UUID>> completedAdvancements, Boolean isFreshWorld) {
        this.completedAdvancements = completedAdvancements;
        this.isFreshWorld = isFreshWorld;
    }

    private static final Codec<StateSaverAndLoader> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    // https://docs.fabricmc.net/develop/codecs#serializing-and-deserializing
                    // https://docs.neoforged.net/docs/datastorage/codecs/
                    // https://docs.neoforged.net/docs/networking/streamcodecs/
                    Codec.unboundedMap(Identifier.CODEC, UUIDUtil.CODEC_SET)
                            // make the unbounded map mutable
                            .xmap(HashMap::new, self -> self)
                            .fieldOf(ADVANCEMENTS_KEY)
                            .forGetter(state -> state.completedAdvancements),
                    Codec.BOOL.fieldOf(FRESH_WORLD_KEY).forGetter(state -> state.isFreshWorld))
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
