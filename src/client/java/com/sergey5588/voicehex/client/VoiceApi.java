package com.sergey5588.voicehex.client;




import com.sergey5588.voicehex.SendSpeechC2SPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.*;
import java.util.LinkedList;
import java.util.Queue;


public class VoiceApi {

    private static Model model;
    private static Recognizer recognizer;
    private static TargetDataLine microphone;
    private static volatile boolean isListening = false;
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
            System.out.println("[Vosk] Model loaded successfully.");
        } catch (Exception e) {
            System.err.println("[Vosk] Failed to load model: " + e.getMessage());
            e.printStackTrace();
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
            AudioFormat format = new AudioFormat(16000.0f, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("[Vosk] Microphone line not supported for format: " + format);
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
                            // Uncomment to see partial results
                            // String partialResult = recognizer.getPartialResult();
                            // System.out.println("Partial: " + partialResult);
                        }
                    }
                }
                cleanup();
            }, "Vosk-Recognition-Thread");

            recognitionThread.start();
            System.out.println("[Vosk] Started listening.");

        } catch (LineUnavailableException e) {
            System.err.println("[Vosk] Microphone unavailable: " + e.getMessage());
            e.printStackTrace();
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
        System.out.println("[Vosk] Stopped listening and cleaned up.");

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
        // Simple parsing to extract the "text" field from JSON
        // For production, use a proper JSON library like Gson
        if (jsonResult.contains("\"text\" : \"")) {
            int start = jsonResult.indexOf("\"text\" : \"") + 10;
            int end = jsonResult.indexOf("\"", start);
            if (end > start) {
                String spokenText = jsonResult.substring(start, end);
                //System.out.println("[Vosk] Recognized: " + spokenText);
                if(MinecraftClient.getInstance().world!=null) {
                    SendSpeechC2SPayload payload = new SendSpeechC2SPayload(spokenText);
                    ClientPlayNetworking.send(payload);
                }


            }
        }
    }

    // === INTEGRATION WITH MINECRAFT FORGE ===

    /**
     * Forge event handler to process results on the client tick.
     * This ensures recognition results are handled on the main game thread.
     */


}