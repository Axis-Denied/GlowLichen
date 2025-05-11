package com.roguetictac.glowlichen.handler;

import com.roguetictac.glowlichen.Tags;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

@Mod.EventBusSubscriber(modid= Tags.MOD_ID)
public class ConfigHandler {
    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent event){
        if(Objects.equals(event.getModID(), Tags.MOD_ID)){
            ConfigManager.sync(Tags.MOD_ID, Config.Type.INSTANCE);
        }

    }
}
