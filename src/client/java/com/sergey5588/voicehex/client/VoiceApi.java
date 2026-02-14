package com.sergey5588.voicehex.client;




import com.google.gson.Gson;
import com.sergey5588.voicehex.SendSpeechC2SPayload;
import com.sergey5588.voicehex.item.custom.MagicWand;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.*;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.logging.Logger;

import static com.sergey5588.voicehex.client.VoicehexClient.LOGGER;
import static com.sergey5588.voicehex.item.ModItems.MAGIC_WAND;


public class VoiceApi {
    private static Gson gson = new Gson();
    private static Model model;
    private static Recognizer recognizer;
    public static TargetDataLine microphone;
    public static volatile boolean isListening = false;
    private static Thread recognitionThread;
    // Thread-safe queue for results to be processed on the main Minecraft thread
    private static final Queue<String> resultQueue = new LinkedList<>();

    /**
     * Initializes the Vosk engine.
     * Call this from your main mod class during initialization (e.g., in preInit or init).
     * @param modelPath The absolute file path to your downloaded Vosk model folder.
     */
    public static void initialize(String modelPath) {
        try {
            model = new Model(modelPath);
            recognizer = new Recognizer(model, 16000.0f);
            LOGGER.info("[Vosk] Model loaded successfully.");
        } catch (Exception e) {

            LOGGER.error("[Vosk] Failed to load model: {}", e.getMessage());
        }

        ClientTickEvents.START_CLIENT_TICK.register((minecraftClient) -> {
                VoiceApi.processResults();
        });
    }

    /**
     * Starts listening to the default microphone.
     */
    public static void startListening() {
        if (isListening || recognizer == null) {
            return;
        }

        try {
            // Configure audio format: 16kHz, 16-bit, mono, signed PCM
            // from https://www.baeldung.com/java-sound-api-capture-mic
            AudioFormat format = new AudioFormat(16000.0f, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                LOGGER.error("[Vosk] Microphone line not supported for format: {}", format);
                return;
            }

            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            isListening = true;
            // Start recognition in a separate thread to avoid blocking the game
            recognitionThread = new Thread(() -> {
                byte[] buffer = new byte[4096]; // Audio buffer
                while (isListening && !Thread.currentThread().isInterrupted()) {
                    int numBytesRead = microphone.read(buffer, 0, buffer.length);
                    if (numBytesRead > 0) {
                        // Process audio chunk with Vosk
                        if (recognizer.acceptWaveForm(buffer, numBytesRead)) {
                            String finalResult = recognizer.getResult();
                            synchronized (resultQueue) {
                                resultQueue.offer(finalResult); // Add result for main thread
                            }
                        } else {

                            String partialResult = gson.fromJson(recognizer.getPartialResult(), speechPartial.class).partial;
//                            if(!partialResult.isEmpty())
//                                LOGGER.info("Partial: {}", partialResult);

                        }
                    }
                }
                cleanup();
            }, "Vosk-Recognition-Thread");
            recognitionThread.start();
//            System.out.println("[Vosk] Started listening.");

        } catch (LineUnavailableException e) {
            LOGGER.error("[Vosk] Microphone unavailable: {}", e.getMessage());
            isListening = false;
        }
    }

    /**
     * Stops listening and releases resources.
     */
    public static void stopListening() {
        isListening = false;
        if (recognitionThread != null) {
            recognitionThread.interrupt();
        }
    }

    /**
     * Cleans up audio and recognizer resources.
     */
    private static void cleanup() {
        if (microphone != null) {
            microphone.stop();
            microphone.close();
        }
        if (recognizer != null) {
            recognizer.close();
        }
        LOGGER.info("[Vosk] Stopped listening and cleaned up.");

    }

    /**
     * Processes any pending recognition results on the main game thread.
     * This method should be called regularly, e.g., on each client tick.
     */
    public static void processResults() {
        String result;
        synchronized (resultQueue) {
            result = resultQueue.poll();
        }
        if (result != null && !result.isEmpty()) {
            // Process the final recognition result here
            onSpeechRecognized(result);
        }
    }

    /**
     * Callback method triggered when speech is recognized.
     * Override this method in your mod to implement custom behavior.
     * @param jsonResult The recognition result in JSON format (contains "text" field).
     */
    private static void onSpeechRecognized(String jsonResult) {
        String text = gson.fromJson(jsonResult, speechResult.class).text;
        if(MinecraftClient.getInstance().world!=null && !text.isEmpty()) {
            SendSpeechC2SPayload payload = new SendSpeechC2SPayload(text);
            ClientPlayNetworking.send(payload);
        }


    }
    static class speechResult {
        String text;
    }
    static class speechPartial {
        String partial;
    }


}