package com.sergey5588.voicehex;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.registry.sync.packet.DirectRegistryPacketHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Spells {
    public static void init() {
        PayloadTypeRegistry.playC2S().register(SendSpeechC2SPayload.ID, SendSpeechC2SPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SendSpeechC2SPayload.ID, Spells::proccessSpell);
    }
    public static void proccessSpell(SendSpeechC2SPayload payload, ServerPlayNetworking.Context context) {
        if(context.player().getWorld() != null) {
            ServerPlayerEntity player = context.player();
            String text = payload.text();
            ServerWorld world = context.player().getServerWorld();


            BlockPos spawnPos = player.getBlockPos().up();
            Vec3d lookVec = player.getRotationVec(1.0F);
            context.player().sendMessage(Text.of(text));
            if(text.contains("fireball")) {
                double speedMultiplier = 200;
                FireballEntity fireball = new FireballEntity(
                        world,
                        player,
                        new Vec3d(
                                lookVec.x * speedMultiplier, // velocityX from look vector
                                lookVec.y * speedMultiplier, // velocityY
                                lookVec.z * speedMultiplier // velocityZ
                        ),
                        5
                );
                fireball.setPosition(spawnPos.getX()+0.5, spawnPos.getY() + 1, spawnPos.getZ()+0.5);
                world.spawnEntity(fireball);
            } else if (text.contains("lightning bolt")) {
                LightningEntity lightning = new LightningEntity(
                        EntityType.LIGHTNING_BOLT,
                        world
                );
                lightning.setPosition(spawnPos.getX()+0.5, spawnPos.getY() + 1, spawnPos.getZ()+0.5);
                world.spawnEntity(lightning);
            }

        }
    }
}
