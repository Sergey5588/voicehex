package com.sergey5588.voicehex.client.custom;

import com.sergey5588.voicehex.Voicehex;
import com.sergey5588.voicehex.client.VoiceApi;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

import static com.sergey5588.voicehex.client.VoicehexClient.MOD_ID;

public class VoiceHUD {
    private static final Identifier VOICE_LAYER = Identifier.of(Voicehex.MOD_ID, "hud-layer");
    public static void initialize() {
        Object IdentifiedLayer;
        HudRenderCallback.EVENT.register(VoiceHUD::render);
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        int color = 0xFFFF0000; // Red
        int targetColor = 0xFF00FF00; // Green

        // You can use the Util.getMeasuringTimeMs() function to get the current time in milliseconds.
        // Divide by 1000 to get seconds.
        double currentTime = Util.getMeasuringTimeMs() / 1000.0;


        // Draw a square with the lerped color.
        // x1, x2, y1, y2, z, color
        Identifier mic_texture;
        if(VoiceApi.microphone!= null && VoiceApi.microphone.isActive()) {
            mic_texture = Identifier.of(MOD_ID, "textures/gui/mic_active.png");
        } else {
            mic_texture = Identifier.of(MOD_ID, "textures/gui/mic_disabled.png");
        }
        context.drawTexture(mic_texture, 0,0,0,0,16,16,16,16);
    }
}
