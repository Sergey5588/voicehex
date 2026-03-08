package com.sergey5588.voicehex;

import com.sergey5588.voicehex.custom.Spell;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.registry.sync.packet.DirectRegistryPacketHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Spells {
    public static enum SpellType {
        FIREBALL,
        MAGIC_MISSILE,
    }
    public static Spell[] all_spells = {
            new Spell("fireball", ctx -> {
                double speedMultiplier = 200;
                Vec3d lookVec = ctx.player().getRotationVec(1.0F);
                FireballEntity fireball = new FireballEntity(
                        ctx.player().getWorld(),
                        ctx.player(),
                        new Vec3d(
                                lookVec.x * speedMultiplier, // velocityX from look vector
                                lookVec.y * speedMultiplier, // velocityY
                                lookVec.z * speedMultiplier // velocityZ
                        ),
                        5
                );
                fireball.setPosition(ctx.player().getX()+0.5, ctx.player().getY() + 1, ctx.player().getZ()+0.5);
                ctx.player().getWorld().spawnEntity(fireball);
            }),
    };


    public static void init() {
        PayloadTypeRegistry.playC2S().register(SendSpeechC2SPayload.ID, SendSpeechC2SPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SendSpeechC2SPayload.ID, Spells::proccessSpell);
    }
    public static void proccessSpell(SendSpeechC2SPayload payload, ServerPlayNetworking.Context context) {
        if(context.player().getWorld() != null) {

            String text = payload.text();
            context.player().sendMessage(Text.of(text));
            for(Spell s: all_spells) {
                if(text.contains(s.name)) s.cast(context);
            }

        }
    }
}
