package com.sergey5588.voicehex.client.renderer;

import com.sergey5588.voicehex.Voicehex;
import com.sergey5588.voicehex.client.renderer.custom.MagicMissileRenderer;
import com.sergey5588.voicehex.entity.ModEntities;
import com.sergey5588.voicehex.entity.custom.MagicMissile;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import static com.sergey5588.voicehex.client.VoicehexClient.MOD_ID;

public class ModRenderers {
    public static void init() {
        EntityRendererRegistry.register(ModEntities.MAGIC_MISSILE_ENTITY_TYPE, ctx ->  new MagicMissileRenderer(ctx));
    }
}
