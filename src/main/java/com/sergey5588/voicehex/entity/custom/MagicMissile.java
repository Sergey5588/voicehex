package com.sergey5588.voicehex.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MagicMissile extends Entity {
    public MagicMissile(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        PlayerEntity closest = this.getEntityWorld().getClosestPlayer(this,100.0);
        if(closest!= null){
            Vec3d target = closest.getPos().subtract(this.getPos());
            this.setVelocity(target.normalize());
            this.velocityModified= true;
            this.velocityDirty = true;
            this.move(MovementType.SELF, this.getVelocity());
            if(target.lengthSquared() <= 2.0f) {
                this.getWorld().createExplosion(this, this.getX(),this.getY(), this.getZ(), 5.0f, World.ExplosionSourceType.MOB);
                this.kill();
            }
        }


    }

    @Override
    public void updatePosition(double x, double y, double z) {
        super.updatePosition(x, y, z);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }


}
