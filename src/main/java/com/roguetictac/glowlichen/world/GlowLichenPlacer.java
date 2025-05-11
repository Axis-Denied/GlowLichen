package com.roguetictac.glowlichen.world;

import com.google.common.collect.Lists;
import com.roguetictac.glowlichen.block.BlockGlowLichen;
import com.roguetictac.glowlichen.config.GlowLichenConfig;
import com.roguetictac.glowlichen.init.GlowLichenBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkGeneratorFlat;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.roguetictac.glowlichen.init.GlowLichenBlocks.GLOW_LICHEN;

public class GlowLichenPlacer implements IWorldGenerator {

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (chunkGenerator instanceof ChunkGeneratorFlat) return;
        boolean found = false;
        for(int dim : GlowLichenConfig.dimensions){
            if(dim == world.provider.getDimension()){
                found = true;
                break;
            }
        }
        if(!found) return;

        int cX = chunkX * 16;
        int cZ = chunkZ * 16;
        for(int i =0;i<GlowLichenConfig.spawnAttempts;i++){
            int x = cX + 8 + random.nextInt(16);
            int z = cZ + 8 + random.nextInt(16);
            int mix = GlowLichenConfig.yMax - GlowLichenConfig.yMin;
            // simulate (poorly) 1.18.2's distribution
            int y = GlowLichenConfig.yMin + mix/2 + (int)(mix*random.nextGaussian()/2);
            BlockPos blockpos = new BlockPos(x, y, z);
            placeLichen(random, world, blockpos);
        }
    }

    private void placeLichen(Random random, World world, BlockPos blockpos) {
            List<EnumFacing> list = getShuffledDirections(random);
            if (!GLOW_LICHEN.placeGlowLichenIfPossible(world, blockpos, random, list)) {
                BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos(blockpos);

                for(EnumFacing direction : list) {
                    mpos.offset(direction);
                    List<EnumFacing> list1 = getShuffledDirectionsExcept(random, direction.getOpposite());

                    for(int j = 0; j < GlowLichenConfig.searchRange; ++j) {
                        mpos.offset(list1.get(random.nextInt(list1.size())));
                        if (GLOW_LICHEN.placeGlowLichenIfPossible(world, mpos, random, list1)) {
                            return;
                        }

                    }

                }

            }

    }

    public static List<EnumFacing> getShuffledDirections(Random p_159850_) {
        List<EnumFacing> list = Lists.newArrayList(EnumFacing.VALUES);
        Collections.shuffle(list, p_159850_);
        return list;
    }

    public static List<EnumFacing> getShuffledDirectionsExcept(Random p_159853_, EnumFacing p_159854_) {
        List<EnumFacing> list = Lists.newArrayList(EnumFacing.VALUES).stream().filter((p_159857_) -> p_159857_ != p_159854_).collect(Collectors.toList());
        Collections.shuffle(list, p_159853_);
        return list;
    }

    private boolean isReplaceable(IBlockState state, World world, BlockPos pos) {
        return state.getBlock().isAir(state, world, pos);
    }
}