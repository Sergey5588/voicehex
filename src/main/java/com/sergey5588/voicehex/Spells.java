package com.sergey5588.voicehex;

import com.sergey5588.voicehex.custom.Spell;
import com.sergey5588.voicehex.entity.custom.MagicMissile;
import com.sun.jna.platform.win32.WinRas;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.registry.sync.packet.DirectRegistryPacketHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

import static com.sergey5588.voicehex.entity.ModEntities.MAGIC_MISSILE_ENTITY_TYPE;

public class Spells {
    private static final double REACH_DISTANCE = 5.0f;
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
                fireball.setPosition(ctx.player().getEyePos());
                ctx.player().getWorld().spawnEntity(fireball);
            }),
            new Spell("magic missile", ctx -> {
                Vec3d lookVec = ctx.player().getRotationVec(1.0F);
                MagicMissile missile = new MagicMissile(ctx.player().getWorld(), ctx.player());
                missile.setPosition(ctx.player().getEyePos());
                ctx.player().getWorld().spawnEntity(missile);
            }),
            new Spell("grow", ctx -> {
                Vec3d camPos = ctx.player().getCameraPosVec(1.0f);
                Vec3d rotation = ctx.player().getRotationVec(1.0f);
                Vec3d end = camPos.add(rotation.multiply(REACH_DISTANCE));
                Box box = ctx.player().getBoundingBox().stretch(rotation.multiply(REACH_DISTANCE)).expand(1,1,1);
                EntityHitResult hit = ProjectileUtil.raycast(ctx.player(),camPos,end,box,e -> !e.isSpectator(), REACH_DISTANCE*REACH_DISTANCE);
                if(hit!=null){
                    if(hit.getEntity() instanceof LivingEntity e) {
                        double scale = e.getAttributeBaseValue(EntityAttributes.GENERIC_SCALE);
                        Objects.requireNonNull(e.getAttributeInstance(EntityAttributes.GENERIC_SCALE)).setBaseValue(scale*1.2);
                    }
                }
            }),
            new Spell("shrink", ctx -> {
                Vec3d camPos = ctx.player().getCameraPosVec(1.0f);
                Vec3d rotation = ctx.player().getRotationVec(1.0f);
                Vec3d end = camPos.add(rotation.multiply(REACH_DISTANCE));
                Box box = ctx.player().getBoundingBox().stretch(rotation.multiply(REACH_DISTANCE)).expand(1,1,1);
                EntityHitResult hit = ProjectileUtil.raycast(ctx.player(),camPos,end,box,e -> !e.isSpectator(), REACH_DISTANCE*REACH_DISTANCE);
                if(hit!=null){
                    if(hit.getEntity() instanceof LivingEntity e) {
                        double scale = e.getAttributeBaseValue(EntityAttributes.GENERIC_SCALE);
                        Objects.requireNonNull(e.getAttributeInstance(EntityAttributes.GENERIC_SCALE)).setBaseValue(scale/1.2);
                    }
                }
            })
    };


    public static void init() {
        PayloadTypeRegistry.playC2S().register(SendSpeechC2SPayload.ID, SendSpeechC2SPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SendSpeechC2SPayload.ID, Spells::proccessSpell);
    }
    public static void proccessSpell(SendSpeechC2SPayload payload, ServerPlayNetworking.Context context) {
        if(context.player().getWorld() != null) {

            String text = payload.text();
            for(Spell s: all_spells) {
                if(text.contains(s.name)) {
                    s.cast(context);

                    context.player().sendMessage(Text.of("Casting: "+s.name), true);
                    break;
                }
            }

        }
    }
}
