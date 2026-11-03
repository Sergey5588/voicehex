package com.sergey5588.voicehex.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Arm;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.sergey5588.voicehex.entity.ModEntities.MAGIC_MISSILE_ENTITY_TYPE;

public class MagicMissile extends ProjectileEntity {
    private int lifetime = 0;
    private static final float EXPLOSION_POWER = 0.5f;
    public Vec3d velocity;
    public MagicMissile(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public MagicMissile(World world, PlayerEntity owner) {
        super(MAGIC_MISSILE_ENTITY_TYPE, world);
        this.setOwner(owner);
        this.setVelocity(owner.getRotationVector());
        this.velocityModified= true;
        this.velocityDirty = true;
    }
    @Override
    public void tick() {
        super.tick();
        Box searchBox = this.getBoundingBox().expand(10.0f);
        HitResult hit = ProjectileUtil.getCollision(this, this::canHit);
        if (hit.getType() != HitResult.Type.MISS) {
            this.onCollision(hit);
            return;
        }
        if(getOwner() != null ) {
            lifetime++;
            this.move(MovementType.SELF, this.getVelocity());
            this.getWorld().addParticle(ParticleTypes.WITCH, this.getX(), this.getY(), this.getZ(),0,0,0);
        }
        if(lifetime >= 100) this.kill();
        if(lifetime >= 10) {
            LivingEntity closest = this.getWorld().getClosestEntity(LivingEntity.class, TargetPredicate.DEFAULT, (LivingEntity)this.getOwner(), this.getX(), this.getY(), this.getZ(), searchBox);
            if(closest!=null) {
                this.setVelocity(closest.getPos().subtract(this.getPos()).normalize());
                this.velocityDirty = true;
            }

        }

    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if(!this.getWorld().isClient()) {
            if(entityHitResult.getEntity() instanceof LivingEntity e) {
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 50,255,false, false,true));
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 50,1,false, false,true));
            }
            this.getWorld().createExplosion(this, entityHitResult.getEntity().getX(), entityHitResult.getEntity().getY(), entityHitResult.getEntity().getZ(), EXPLOSION_POWER, World.ExplosionSourceType.MOB);

            this.kill();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult bl) {
        if(!this.getWorld().isClient()) {
            this.getWorld().createExplosion(this, bl.getBlockPos().getX(),bl.getBlockPos().getY(), bl.getBlockPos().getZ(), EXPLOSION_POWER, World.ExplosionSourceType.BLOCK);
            this.kill();
        }
    }

    @Override
    public void updatePosition(double x, double y, double z) {
        super.updatePosition(x, y, z);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }



}
