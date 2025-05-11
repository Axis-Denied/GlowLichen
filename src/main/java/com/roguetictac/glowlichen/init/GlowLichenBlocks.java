package com.roguetictac.glowlichen.init;

import com.roguetictac.glowlichen.Tags;
import com.roguetictac.glowlichen.block.BlockGlowLichen;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemColored;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;

@Mod.EventBusSubscriber(modid= Tags.MOD_ID)
public class GlowLichenBlocks {

    public static BlockGlowLichen GLOW_LICHEN = new BlockGlowLichen("glow_lichen");
    public static Item GLOW_LICHEN_ITEM;


    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(GLOW_LICHEN);
    }
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        GLOW_LICHEN_ITEM = new ItemColored(GLOW_LICHEN, false).setRegistryName(Objects.requireNonNull(GLOW_LICHEN.getRegistryName()));
        registry.register(GLOW_LICHEN_ITEM);
    }
    @Mod.EventBusSubscriber(value= Side.CLIENT, modid= Tags.MOD_ID)
    public static class GlowLichenModels {

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event) {

            ModelLoader.setCustomModelResourceLocation(GLOW_LICHEN_ITEM, 0, new ModelResourceLocation(Objects.requireNonNull(GLOW_LICHEN.getRegistryName()), "inventory"));
        }
    }

}
