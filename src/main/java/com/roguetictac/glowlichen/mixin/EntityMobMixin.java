package com.roguetictac.glowlichen.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.roguetictac.glowlichen.config.GlowLichenConfig;
import net.minecraft.entity.monster.EntityMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityMob.class)
public class EntityMobMixin {


    @ModifyExpressionValue(method="isValidLightLevel", at= @At(value = "CONSTANT", args = "intValue=8"))
    public int glowlichen$isValidLightLevel(int original, @Local(name="i") int i){
        System.out.println("Calculated light level: "+i);
        if(GlowLichenConfig.modifyLightLevel < 0) return original;
        return GlowLichenConfig.modifyLightLevel+1;
    }
}
