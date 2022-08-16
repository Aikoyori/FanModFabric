package xyz.aikoyori.bigfan.entities;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.*;
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
import net.minecraft.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.aikoyori.bigfan.Bigfan;

import java.util.UUID;

import static xyz.aikoyori.bigfan.utils.FanHelper.getCollisionFromVector;
import static xyz.aikoyori.bigfan.utils.FanHelper.getOutlineCollision;

public class FanWindEntity extends Entity {
    private static final TrackedData<Float> LIFE = DataTracker.registerData(FanWindEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> FAN_ROT_SPD = DataTracker.registerData(FanWindEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> ON_FIRE = DataTracker.registerData(FanWindEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public FanWindEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public FanEntity getFanOwner() {
        return fanOwner;
    }

    public void setFanOwner(FanEntity fanOwner) {
        this.fanOwner = fanOwner;
    }

    public void setFanRotSpd(float fanRotSpd) {
        getDataTracker().set(FAN_ROT_SPD,fanRotSpd);
    }
    public float getFanRotSpd() {
        return getDataTracker().get(FAN_ROT_SPD);
    }

    FanEntity fanOwner;
    public UUID getFanHolder() {
        return fanHolder;
    }

    public void setFanHolder(UUID fanHolder) {
        this.fanHolder = fanHolder;
    }

    public UUID fanHolder = null;

    public int getFanPowerLevel() {
        return fanPowerLevel;
    }

    public void setFanPowerLevel(int fanPowerLevel) {
        this.fanPowerLevel = fanPowerLevel;
    }

    public int fanPowerLevel = 0;
    int particleType=0;
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
    public void setWindOnFire(boolean life){
        this.dataTracker.set(ON_FIRE,life);
    }
    public boolean getWindOnFire(){
        return this.dataTracker.get(ON_FIRE);
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
        // TODO : ADD BUBBLES INSTEAD OF CLOUD WHEN UNDERWATER
        // TODO : ADD FIRE TYPE THAT HURTS :troll:
        // TODO : EXTINGUISH FIRE IF WIND IS NOT FIRE OR IS OF WATER TYPE
        if(isSubmergedIn(FluidTags.LAVA))
        {
            setWindOnFire(true);
        }

        if(isSubmergedIn(FluidTags.WATER))
        {

            setWindOnFire(false);
            particleType=2;
        }
        else{

            if(getWindOnFire())
                particleType = 1;
            else
                particleType=0;
        }



        Vec3d norm = getVelocity().normalize().multiply(particleType==0?0.3:0.03);
        norm = norm.multiply(getFanRotSpd()/10.0f);
        Vec3d spwnL = new Vec3d(getX()+(random.nextFloat()-0.5f)*0.2,getY()+(random.nextFloat()-0.5f)*0.2,getZ()+(random.nextFloat()-0.5f)*0.2);

        if(particleType==0)
        spwnL = spwnL.add(norm.multiply(-10f));
        if(this.random.nextFloat()>0.85f+0.02f*getFanPowerLevel())
        world.addParticle(
                switch (particleType)
                        {
                            case 1 -> ParticleTypes.FLAME;
                            case 2 -> ParticleTypes.BUBBLE;
                            default -> Bigfan.LEAF_BLOW;
                        },spwnL.getX(),spwnL.getY(),spwnL.getZ(),norm.getX(),norm.getY(),norm.getZ());

        HitResult hitResult2 = ProjectileUtil.getCollision(this, this::canHit);
        HitResult hitResult3 = getCollisionFromVector(this, this::canHit,getVelocity().multiply(-1));
        HitResult hitResult = getOutlineCollision(this, this::canHit);
        if(hitResult.getType()== HitResult.Type.BLOCK)
        {
            BlockHitResult hitB = (BlockHitResult) hitResult;
            if(getWindOnFire())
            {

                if(FlammableBlockRegistry.getInstance(Blocks.FIRE).get(world.getBlockState(hitB.getBlockPos()).getBlock()).getBurnChance()>0)
                {
                    BlockPos sthin = new BlockPos(
                            hitB.getBlockPos().getX()+hitB.getSide().getVector().getX()
                            ,hitB.getBlockPos().getY()+hitB.getSide().getVector().getY()
                            ,hitB.getBlockPos().getZ()+hitB.getSide().getVector().getZ()
                    );
                    world.setBlockState(sthin,Blocks.FIRE.getDefaultState());
                }
            }


            if(world.getBlockState(hitB.getBlockPos()).getBlock() instanceof AbstractFireBlock)
            {
                if(!this.getWindOnFire()){
                    world.setBlockState(hitB.getBlockPos(),Blocks.AIR.getDefaultState());
                }
            }

        }
        if(hitResult2.getType()== HitResult.Type.BLOCK && hitResult3.getType()== HitResult.Type.BLOCK)
        {
            BlockHitResult bhr2 = ((BlockHitResult)hitResult2);
            BlockHitResult bhr3 = ((BlockHitResult)hitResult3);
            if ((Block.isFaceFullSquare(
                            world.getBlockState(bhr2.getBlockPos()).getSidesShape(world, bhr2.getBlockPos()),bhr2.getSide())) ||
                    (Block.isFaceFullSquare(
                            world.getBlockState(bhr3.getBlockPos()).getSidesShape(world, bhr3.getBlockPos()),bhr3.getSide()))
            )
            {
                setLife(0);
            }



        }
        if(hitResult.getType()== HitResult.Type.ENTITY)
        {
            EntityHitResult hitE = (EntityHitResult) hitResult;
            Vec3d addVel = new Vec3d(this.getVelocity().x,this.getVelocity().y,this.getVelocity().z).multiply(0.5);
            if(getFanHolder()!=null && hitE.getEntity().getUuid().equals(getFanHolder()))
            {

            }
                else
            {

                hitE.getEntity().addVelocity(addVel.getX(),addVel.getY(),addVel.getZ());
            }
            if(getWindOnFire())
            {
                hitE.getEntity().setFireTicks(20);
            }
            else
            {
                hitE.getEntity().setFireTicks(-1);

            }
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
        this.getDataTracker().startTracking(ON_FIRE,false);
        this.getDataTracker().startTracking(FAN_ROT_SPD,0.0f);
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
