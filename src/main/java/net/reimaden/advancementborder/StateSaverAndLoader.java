package net.reimaden.advancementborder;

import net.fabricmc.fabric.api.tag.convention.v2.TagUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class StateSaverAndLoader extends SavedData {
    private static final String ADVANCEMENTS_KEY = "completedAdvancements";
    private static final String FRESH_WORLD_KEY = "isFreshWorld";
    public Map<ResourceLocation, Set<UUID>> completedAdvancements = new HashMap<>();
    public boolean isFreshWorld = true;

    @Override
    public @NotNull CompoundTag save(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        CompoundTag advancementsTag = new CompoundTag();
        completedAdvancements.forEach((advancement, uuids) -> {
            ListTag uuidsTag = new ListTag();
            for (UUID uuid : uuids) {
                uuidsTag.add(new IntArrayTag(UUIDUtil.uuidToIntArray(uuid)));
            }

            advancementsTag.put(advancement.toString(), uuidsTag);
        });
        nbt.put(ADVANCEMENTS_KEY, advancementsTag);
        nbt.putBoolean(FRESH_WORLD_KEY, isFreshWorld);
        return nbt;
    }

    private static StateSaverAndLoader createFromNbt(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        StateSaverAndLoader state = new StateSaverAndLoader();

        CompoundTag advancementsTag = nbt.getCompound(ADVANCEMENTS_KEY);
        for (String location : advancementsTag.getAllKeys()) {
            ResourceLocation id = ResourceLocation.parse(location);

            Set<UUID> uuids = new HashSet<>();
            state.completedAdvancements.put(id, uuids);

            ListTag uuidsTag = advancementsTag.getList(location, Tag.TAG_INT_ARRAY);
            uuidsTag.forEach(u -> {
                IntArrayTag tag = (IntArrayTag) u;
                // Int-array represtation of a UUID
                // See https://minecraft.wiki/w/UUID
                UUID uuid = UUIDUtil.uuidFromIntArray(new int[] {
                    tag.get(0).getAsInt(),
                    tag.get(1).getAsInt(),
                    tag.get(2).getAsInt(),
                    tag.get(3).getAsInt(),
                });
                uuids.add(uuid);
            });
        }
        state.isFreshWorld = nbt.getBoolean(FRESH_WORLD_KEY);
        return state;
    }

    private static final Factory<StateSaverAndLoader> TYPE = new Factory<>(
            StateSaverAndLoader::new,
            StateSaverAndLoader::createFromNbt,
            null
    );

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        ServerLevel world = server.overworld();
        DimensionDataStorage manager = world.getDataStorage();
        return manager.computeIfAbsent(TYPE, AdvancementBorder.MOD_ID);
    }
}
