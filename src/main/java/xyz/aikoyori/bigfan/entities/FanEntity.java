package xyz.aikoyori.bigfan.entities;

import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.aikoyori.bigfan.Bigfan;
import xyz.aikoyori.bigfan.utils.FanHelper;

public class FanEntity extends Entity {
    private static final TrackedData<Boolean> SWINGING =  DataTracker.registerData(FanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Float> SWING_PROGRESS =  DataTracker.registerData(FanEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> FAN_POWER =  DataTracker.registerData(FanEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> FAN_SWING_SPEED =  DataTracker.registerData(FanEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> FAN_BLADE_ROTATION =  DataTracker.registerData(FanEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> FAN_BLADE_ROTATION_SPEED =  DataTracker.registerData(FanEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> FAN_BLADE_ROTATION_ACCELERATION =  DataTracker.registerData(FanEntity.class, TrackedDataHandlerRegistry.FLOAT);

    private final TrackedPosition trackedPosition;
    private float prevBladeRot;
    private float prevSwingProg = 0.0f;
    private float rotSpeedLimit = 0.0f;
    public FanEntity(EntityType<?> type, World world) {
        super(type, world);
        intersectionChecked = true;
        trackedPosition = new TrackedPosition();
    }

    @Override
    public void initDataTracker() {

        this.dataTracker.startTracking(SWINGING, false);
        this.dataTracker.startTracking(SWING_PROGRESS, 0.0f);
        this.dataTracker.startTracking(FAN_POWER, 0);
        this.dataTracker.startTracking(FAN_SWING_SPEED, 0.000f);
        this.dataTracker.startTracking(FAN_BLADE_ROTATION, 0.00f);
        this.dataTracker.startTracking(FAN_BLADE_ROTATION_SPEED, 0.00f);
        this.dataTracker.startTracking(FAN_BLADE_ROTATION_ACCELERATION, 0.00f);


    }

    public boolean isSwinging(){
        return getDataTracker().get(SWINGING);
    }
    public void setSwinging(boolean swing){
        getDataTracker().set(SWINGING,swing);
    }

    public float getSwingSpeed(){
        return getDataTracker().get(FAN_SWING_SPEED);
    }
    public void setSwingSpeed(float speed){
        getDataTracker().set(FAN_SWING_SPEED,speed);
    }
    public float getRotationSpeed(){
        return getDataTracker().get(FAN_BLADE_ROTATION_SPEED);
    }
    public void setRotationSpeed(float speed){
        getDataTracker().set(FAN_BLADE_ROTATION_SPEED,speed);
    }
    public float getRotationAcceleration(){
        return getDataTracker().get(FAN_BLADE_ROTATION_ACCELERATION);
    }
    public void setRotationAcceleration(float acc){
        getDataTracker().set(FAN_BLADE_ROTATION_ACCELERATION,acc);
    }
    public float getBladeRotation(){
        return getDataTracker().get(FAN_BLADE_ROTATION);
    }
    public void setBladeRotation(float speed){
        getDataTracker().set(FAN_BLADE_ROTATION,MathHelper.wrapDegrees(speed));
    }
    public float getSwingingProgress(){
        return getDataTracker().get(SWING_PROGRESS);
    }
    public void setSwingingProgress(float swing){
        getDataTracker().set(SWING_PROGRESS,swing);
    }

    public int getFanPower(){
        return getDataTracker().get(FAN_POWER);
    }
    public void setFanPower(int level){
        getDataTracker().set(FAN_POWER,level);
    }

    @Override
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        /*
        float[] sus = FanHelper.Vec3dtoDegrees(hitPos.normalize());
        float yaw = sus[0]-this.getYaw();
        if(player.getMainHandStack().isEmpty())
        {

            if(MathHelper.abs(yaw)>90)
            {


                setSwinging(!isSwinging());
                setSwingSpeed(isSwinging()? 0.005f:0f);
                this.playSound(SoundEvents.UI_BUTTON_CLICK, 8f,0.5f);
                return ActionResult.SUCCESS;
            }
            else
            {

                scrollFanNumber(player.isSneaking()?-1:+1);
                return ActionResult.SUCCESS;
            }
        }*/



        return super.interactAt(player, hitPos, hand);
    }

    public void scrollFanNumber(int add){
        int fanTarget = (getFanPower()+add);
        do{
            fanTarget+=5;
        }
        while(fanTarget<0);
        setFanPower(fanTarget%5);
        float pitch = switch(getFanPower())
                {
                    case 0:
                        yield 0f;
                    case 1:
                        yield 1f;
                    case 2:
                        yield 1.25f;
                    case 3:
                        yield 1.5f;
                    case 4:
                        yield 1.75f;
                    default:
                        yield 1f;
                };
        this.playSound(SoundEvents.UI_BUTTON_CLICK, 8f,pitch);
    }

    public float getPrevBladeRot() {
        return prevBladeRot;
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        this.intersectionChecked = true;
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        this.setPosition(d, e, f);
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public boolean isCollidable() {
        return super.isCollidable();//true;
    }

    public float getDegreeToBlow(){
        return -((float)(MathHelper.sin(((float)((getSwingingProgress())*Math.PI*2.0f)))*(Math.PI/4.0f)))/(MathHelper.PI*2)*360.0f;
    }
    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {

        if(player.getMainHandStack().isEmpty())
        {
            if(player.isSneaking())
            {
                setSwinging(!isSwinging());
                this.playSound(SoundEvents.UI_BUTTON_CLICK, 8f,0.5f);
            }
            else
                scrollFanNumber(1);
            return ActionResult.SUCCESS;
        }


        return super.interact(player, hand);
    }

    public boolean damage(DamageSource source, float amount) {
        if (!this.world.isClient && !this.isRemoved()) {
            if (this.isInvulnerableTo(source)) {
                return false;
            } else {
                boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).getAbilities().creativeMode;
                if (true) {
                    this.removeAllPassengers();
                    if (bl && !this.hasCustomName()) {
                        this.discard();
                    } else {
                        this.dropItems(source);
                    }
                }

                return true;
            }
        } else {
            return true;
        }
    }

    public boolean canHit() {
        return !this.isRemoved();
    }
    public void updateTrackedPosition(double x, double y, double z) {
        this.trackedPosition.setPos(new Vec3d(x, y, z));
    }

    public TrackedPosition getTrackedPosition() {
        return this.trackedPosition;
    }
    public void dropItems(DamageSource damageSource) {
        this.kill();
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            ItemStack itemStack = new ItemStack(this.getItem());
            if (this.hasCustomName()) {
                itemStack.setCustomName(this.getCustomName());
            }

            this.dropStack(itemStack);
        }

    }

    Item getItem(){
        return Bigfan.FAN_ITEM;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }


    /*
    public float getBladeRotation(float tickDelta) {
        return tickDelta == 1.0F ? this.swingYaw : MathHelper.lerp(tickDelta, this.prevSwingYaw, this.swingYaw);
    }*/

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {


        getDataTracker().set(SWINGING,nbt.getBoolean("Swinging"));
        getDataTracker().set(FAN_POWER,nbt.getInt("FanPower"));
        getDataTracker().set(FAN_SWING_SPEED,nbt.getFloat("FanSwingSpeed"));
        getDataTracker().set(SWING_PROGRESS,nbt.getFloat("SwingProgress"));
        getDataTracker().set(FAN_BLADE_ROTATION,nbt.getFloat("FanRotation"));
        getDataTracker().set(FAN_BLADE_ROTATION_SPEED,nbt.getFloat("FanRotationSpeed"));
        getDataTracker().set(FAN_BLADE_ROTATION_ACCELERATION,nbt.getFloat("FanRotationAccel"));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {

        nbt.putBoolean("Swinging",getDataTracker().get(SWINGING));
        nbt.putInt("FanPower",getDataTracker().get(FAN_POWER));
        nbt.putFloat("FanSwingSpeed",getDataTracker().get(FAN_SWING_SPEED));
        nbt.putFloat("SwingProgress",getDataTracker().get(SWING_PROGRESS));
        nbt.putFloat("FanRotationSpeed",getDataTracker().get(FAN_BLADE_ROTATION_SPEED));
        nbt.putFloat("FanRotation",getDataTracker().get(FAN_BLADE_ROTATION));
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }


    @Override
    public void tick() {
        double d;
        super.tick();
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
        prevBladeRot = getBladeRotation();
        prevYaw = getYaw();
        setSwingSpeed(isSwinging()? 0.005f:0f);
        switch (getFanPower())
        {
            case 0:

                setRotationAcceleration(0f);
                if(MathHelper.abs(getRotationSpeed()*4.0f/5.0f)>=0.001f){
                setRotationSpeed(getRotationSpeed()*4.0f/5.0f);
                }
                else
                {
                    setRotationSpeed(0f);
                }
                break;
            case 1: setRotationAcceleration(0.01f); rotSpeedLimit = 1f; break;
            case 2: setRotationAcceleration(0.02f); rotSpeedLimit = 2f;break;
            case 3: setRotationAcceleration(0.03f); rotSpeedLimit = 3f;break;
            case 4: setRotationAcceleration(0.5f); rotSpeedLimit = 5f; break;
        }
        if(getFanPower()!=0) setRotationSpeed(MathHelper.clamp(getRotationSpeed()+getRotationAcceleration(),-10.0f,10.0f));
        setBladeRotation(getBladeRotation()+MathHelper.clamp(getRotationSpeed(),-rotSpeedLimit,rotSpeedLimit));


        //this.setVelocity(this.getVelocity().multiply(0.98));

        //System.out.println((world.isClient?"client":"server")+" has " + " " +getPos());
        if(!this.hasNoGravity())

            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));

        //this.setPosition(this.getX()+getVelocity().getX(),this.getY()+getVelocity().getY(),this.getZ()+getVelocity().getZ());
        this.move(MovementType.SELF, this.getVelocity());

        float f = 0.98F;
        if (this.onGround) {
            f = this.world.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0, this.getZ())).getBlock().getSlipperiness() * 0.98F;
        }

        this.setVelocity(this.getVelocity().multiply((double)f, 0.98, (double)f));
        if (this.onGround) {
            this.setVelocity(this.getVelocity().multiply(1.0, -0.9, 1.0));
        }

        //this.checkBlockCollision();

        updatePositionAndAngles(this.getX(),this.getY(),this.getZ(),this.getYaw(),this.getPitch());
        prevSwingProg = getSwingingProgress();
        if( this.getFanPower()>0 && this.isSwinging())
        {
            this.setSwingingProgress(this.getSwingingProgress()+this.getSwingSpeed());
        }
        Vec3d blowFrom = getPos();
        blowFrom = blowFrom.add(Vec3d.fromPolar(0f,getYaw()+getDegreeToBlow()).normalize().multiply(-3/16f));
        //blowFrom.add(0f,0.75f,0f);
        Vec3d blowWay = Vec3d.fromPolar(0f,getYaw()+getDegreeToBlow()).normalize().multiply((getRotationSpeed()/20f));


        //System.out.println(getDegreeToBlow());
        if(getRotationSpeed()>0)
        {

            FanWindEntity wind = new FanWindEntity(Bigfan.FAN_WIND_ENTITY,world);
            wind.setPosition(blowFrom.add(0f,0.75f,0f));
            wind.setVelocity(blowWay);
            wind.setLife(getRotationSpeed()*3);
            wind.setYaw(getYaw());
            world.spawnEntity(wind);
        }
        checkBlockCollision();
        /*
        if (!this.world.isSpaceEmpty(this.getBoundingBox())) {
            this.pushOutOfBlocks(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 1.0, this.getZ());
        }*/
    }
    public boolean isLogicalSideForUpdatingMovement() {
        Entity entity = this.getPrimaryPassenger();
        if (entity instanceof PlayerEntity) {
            return ((PlayerEntity)entity).isMainPlayer();
        } else {
            return !this.world.isClient;
        }
    }

    @Nullable
    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(getItem());
    }

    public float getPrevSwingProg() {
        return prevSwingProg;
    }


    public void setPrevSwingProg(float prevSwingProg) {
        this.prevSwingProg = prevSwingProg;
    }
}
