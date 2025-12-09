package com.sergey5588.voicehex.client;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static net.fabricmc.loader.impl.FabricLoaderImpl.MOD_ID;

public record SendSpeechC2SPayload(String text) implements CustomPayload {
    public static final Identifier SPEECH_PACKET_ID = Identifier.of(MOD_ID, "speech_packet");
    public static final CustomPayload.Id<SendSpeechC2SPayload> ID = new CustomPayload.Id<>(SPEECH_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SendSpeechC2SPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, SendSpeechC2SPayload::text, SendSpeechC2SPayload::new);
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
