package com.roguetictac.glowlichen.config;

import com.roguetictac.glowlichen.Tags;
import net.minecraftforge.common.config.Config;

@Config(modid= Tags.MOD_ID, name="glowlichen")
public class GlowLichenConfig {

    @Config.Name("Spawning Light Level")
    @Config.Comment("The new maximum light level for spawning. Use -1 to indicate no change")
    public static int modifyLightLevel = 0;


    @Config.Name("Chunk Spawn Attempts")
    @Config.Comment("The amount of times to attempt to place Glow Lichen per chunk.")
    public static int spawnAttempts = 100;


    @Config.Name("Glow Lichen Dimension IDs")
    @Config.Comment("Dimensions where Glow Lichen can spawn naturally.")
    public static int[] dimensions = {
            0
    };
    @Config.Name("Chance of Spreading")
    @Config.Comment("The chance for Glow Lichen to 'spread' when generating.")
    public static double chanceOfSpreading = 0.5f;
    @Config.Name("Search Range")
    @Config.Comment("Amount of blocks to search for viable placement positions if the current one fails.")
    @Config.RangeInt(min=0, max=7)
    public static double searchRange = 6;
    @Config.Name("Blob Size")
    @Config.Comment("The maximum amount of extra radius to add to the glow lichen placement when spreading.")
    @Config.RangeInt(min=0)
    public static int blobSize = 3;

    @Config.Name("Maximum Placement Height")
    @Config.Comment("The highest coordinate Glow Lichen can generate on.")
    public static int yMax = 60;
    @Config.Name("Minimum Placement Height")
    @Config.Comment("The lowest coordinate Glow Lichen can generate on.")
    public static int yMin = 2;
}
