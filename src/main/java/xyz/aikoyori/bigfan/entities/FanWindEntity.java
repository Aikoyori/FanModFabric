package xyz.aikoyori.bigfan.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FanWindEntity extends Entity {
    private static final TrackedData<Float> LIFE = DataTracker.registerData(FanWindEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public FanWindEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public boolean shouldRender(double distance) {
        return false;
    }

    public void setLife(float life){
        this.dataTracker.set(LIFE,life);
    }
    public float getLife(){
        return this.dataTracker.get(LIFE);
    }
    protected boolean canHit(Entity entity) {
        if (!entity.isSpectator() && entity.isAlive() && entity.canHit() && !(entity instanceof FanEntity)&& !(entity instanceof FanWindEntity)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.setPosition(this.getPos().add(this.getVelocity()));

        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        if(hitResult.getType()== HitResult.Type.BLOCK)
        {
            setLife(0);
        }
        if(hitResult.getType()== HitResult.Type.ENTITY)
        {
            EntityHitResult hitE = (EntityHitResult) hitResult;
            Vec3d addVel = new Vec3d(this.getVelocity().x,this.getVelocity().y,this.getVelocity().z).multiply(0.5);
            hitE.getEntity().addVelocity(addVel.getX(),addVel.getY(),addVel.getZ());
            hitE.getEntity().velocityModified = true;
            if(hitE.getEntity() instanceof LivingEntity){
                LivingEntity target = (LivingEntity) hitE.getEntity();
                if (target instanceof ServerPlayerEntity && target.velocityModified) {
                    ((ServerPlayerEntity)target).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
                    target.velocityModified = false;
                }
            }

            //setLife(0);
        }
        if(getLife()<=0)
        {
            this.discard();
        }
        else
        {
            this.setLife(this.getLife()-1);
        }
        world.addParticle(ParticleTypes.CLOUD,getX(),getY(),getZ(),0,0,0);
        updatePositionAndAngles(this.getX(),this.getY(),this.getZ(),this.getYaw(),this.getPitch());
    }

    @Override
    public boolean collidesWith(Entity other) {
        return super.collidesWith(other);
    }

    @Override
    public boolean isCollidable() {
        return super.isCollidable();
    }


    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(LIFE,0f);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
