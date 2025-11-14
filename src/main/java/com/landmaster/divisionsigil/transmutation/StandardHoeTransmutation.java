package com.landmaster.divisionsigil.transmutation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public record StandardHoeTransmutation(
        BlockPredicate inputBlock,
        HolderSet<Biome> biomeList,
        boolean isBiomeAllowlist,
        WeightedRandomList<WeightedEntry.Wrapper<BlockState>> outputBlock,
        int priority
) implements HoeTransmutation {
    public static final MapCodec<StandardHoeTransmutation> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BlockPredicate.CODEC.fieldOf("input").forGetter(StandardHoeTransmutation::inputBlock),
                    RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("biomes", HolderSet.empty()).forGetter(StandardHoeTransmutation::biomeList),
                    Codec.BOOL.optionalFieldOf("isBiomeAllowlist", true).forGetter(StandardHoeTransmutation::isBiomeAllowlist),
                    WeightedRandomList.codec(WeightedEntry.Wrapper.codec(BlockState.CODEC)).fieldOf("output").forGetter(StandardHoeTransmutation::outputBlock),
                    Codec.INT.optionalFieldOf("priority", 0).forGetter(StandardHoeTransmutation::priority)
            ).apply(instance, StandardHoeTransmutation::new));

    @Override
    public Optional<BlockState> newBlockState(ServerLevel level, BlockPos pos) {
        if (inputBlock.matches(level, pos)
                && (biomeList.size() == 0 || (isBiomeAllowlist == biomeList.contains(level.getBiome(pos))))) {
            return outputBlock.getRandom(level.random).map(WeightedEntry.Wrapper::data);
        }
        return Optional.empty();
    }

    @Override
    public MapCodec<StandardHoeTransmutation> type() {
        return CODEC;
    }
}
