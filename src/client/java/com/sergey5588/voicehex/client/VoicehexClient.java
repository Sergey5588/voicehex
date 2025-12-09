package com.sergey5588.voicehex.client;

import com.sergey5588.voicehex.SendSpeechC2SPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;



public class VoicehexClient implements ClientModInitializer {
    public static final String MOD_ID = "voicehex";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Path CONF_DIR = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);

    @Override
    public void onInitializeClient() {
        File dir = new File(CONF_DIR.toString());
        if(!dir.mkdir()) {
            LOGGER.error("Failed to create dir: " + dir.getName());
        }

        VoiceApi.initialize(CONF_DIR.resolve("vosk-model-small-en-us-0.15").toString());

        VoiceApi.startListening();
    }

}
