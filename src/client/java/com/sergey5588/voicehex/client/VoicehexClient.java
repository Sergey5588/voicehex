package com.sergey5588.voicehex.client;

import com.sergey5588.voicehex.client.custom.VoiceHUD;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.sergey5588.voicehex.item.ModItems.MAGIC_WAND;

// Method to unzip files

public class VoicehexClient implements ClientModInitializer {
    public static final String MOD_ID = "voicehex";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Path CONF_DIR = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
    public static Boolean canListen = true;

    public void unzipModel(String zipFile, String destFolder) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(getClass().getResourceAsStream(zipFile))) {
            ZipEntry entry;
            byte[] buffer = new byte[1024];
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(destFolder + File.separator + entry.getName());
                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onInitializeClient() {

        File dir = new File(CONF_DIR.toString());
        if(!dir.mkdir() && !dir.exists()) {
            LOGGER.error("Failed to create dir: {}", dir.getName());
        }
        try {
            unzipModel("/vosk-model-small-en-us-0.15.zip", dir.getPath().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        VoiceApi.initialize(CONF_DIR.resolve("vosk-model-small-en-us-0.15").toString());
        VoiceHUD.initialize();
        if(canListen) {
            VoiceApi.startListening();

        }
        ClientTickEvents.START_CLIENT_TICK.register((minecraftClient) -> {
            if(MinecraftClient.getInstance().player!=null && MinecraftClient.getInstance().player.getMainHandStack().isOf(MAGIC_WAND)) {
                if(VoiceApi.microphone!= null && !VoiceApi.microphone.isActive()) {
                    VoiceApi.microphone.flush();
                    VoiceApi.microphone.start();
                }

            } else {
                if(VoiceApi.microphone!= null && VoiceApi.microphone.isActive())
                    VoiceApi.microphone.stop();

            }
        });
    }

}
