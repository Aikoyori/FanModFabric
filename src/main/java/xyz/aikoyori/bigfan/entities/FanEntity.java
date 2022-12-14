package xyz.aikoyori.bigfan.entities;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.aikoyori.bigfan.Bigfan;

import java.util.Optional;
import java.util.UUID;

public class FanEntity extends Entity {
    private static final TrackedData<Boolean> SWINGING =  DataTracker.registerData(FanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Float> SWING_PROGRESS =  DataTracker.registerData(FanEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> FAN_POWER =  DataTracker.registerData(FanEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> FAN_SWING_SPEED =  DataTracker.registerData(FanEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> FAN_BLADE_ROTATION =  DataTracker.registerData(FanEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> FAN_BLADE_ROTATION_SPEED =  DataTracker.registerData(FanEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> FAN_BLADE_ROTATION_ACCELERATION =  DataTracker.registerData(FanEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> IS_BEING_HELD =  DataTracker.registerData(FanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Optional<UUID>> HELD_BY =  DataTracker.registerData(FanEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<Integer> HELD_BY_CLIENT_SYNC =  DataTracker.registerData(FanEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DAMAGE_TILT =  DataTracker.registerData(FanEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> PREV_DMG_TILT =  DataTracker.registerData(FanEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> LOCKED =  DataTracker.registerData(FanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private UUID heldBy;

    private final TrackedPosition trackedPosition;
    private float prevBladeRot;

    public int getHitTimer() {
        return this.getDataTracker().get(DAMAGE_TILT);
    }
    public float getLerpedHitTimer(float tickDelta) {
        return MathHelper.lerp(MathHelper.sin(getPrevHitTimer()),MathHelper.sin(getHitTimer()),tickDelta)*getHitTimer();
    }

    public int getHitTimer(float tickDelta) {
        return this.getDataTracker().get(DAMAGE_TILT);
    }

    public void setHitTimer(int hitTimerx) {
        prevHitTimer = this.hitTimer;
        this.getDataTracker().set(DAMAGE_TILT,prevHitTimer);
        this.hitTimer = hitTimerx;
        this.getDataTracker().set(DAMAGE_TILT,hitTimer);
    }

    public int getPrevHitTimer() {
        return this.getDataTracker().get(PREV_DMG_TILT);
    }
    public int getPrevHitTimer(float tickDelta) {
        return this.getDataTracker().get(PREV_DMG_TILT);
    }


    private int hitTimer = 0;
    private int prevHitTimer = 0;


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
        this.dataTracker.startTracking(IS_BEING_HELD, false);
        this.dataTracker.startTracking(HELD_BY, Optional.empty());
        this.dataTracker.startTracking(HELD_BY_CLIENT_SYNC, -1);
        this.dataTracker.startTracking(DAMAGE_TILT, 0);
        this.dataTracker.startTracking(PREV_DMG_TILT, 0);
        this.dataTracker.startTracking(LOCKED, false);


    }

    public boolean isSwinging(){
        return getDataTracker().get(SWINGING);
    }
    public void setSwinging(boolean swing){
        getDataTracker().set(SWINGING,swing);
    }
    public boolean isLocked(){
        return getDataTracker().get(LOCKED);
    }
    public void setLocked(boolean lock){
        getDataTracker().set(LOCKED,lock);
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
        int oldFanPower = getFanPower();

        setFanPower(fanTarget%5);


        float pitch = switch(getFanPower())
                {
                    case 0:
                        yield 1f;
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

        if(oldFanPower == 0)
        {
            this.playSound(Bigfan.FAN_BUTTON_SNDEVT, 0.4f,pitch);
        }
        else if(getFanPower()==0)
        {

            this.playSound(Bigfan.FAN_POWEROFF_SNDEVT, 0.4f,pitch);
        }
        else
        {
            this.playSound(Bigfan.FAN_BUTTON_SWITCH_EVT, 0.4f,pitch);
        }

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
    double targetX;
    double targetY;
    double targetZ;
    int targetTicks;
    public boolean isBeingHeld() {
        return this.getDataTracker().get(IS_BEING_HELD);
    }
    public void setBeingHeld(boolean ibh) {
        this.getDataTracker().set(IS_BEING_HELD,ibh);
    }
    public UUID getBeingHeldBy() {
        return dataTracker.get(HELD_BY).isEmpty()?null:dataTracker.get(HELD_BY).get();
    }
    public int getBeingHeldByClientSync() {
        return dataTracker.get(HELD_BY_CLIENT_SYNC);
    }
    public void setBeingHeldBy(Optional<UUID> ibh) {
        dataTracker.set(HELD_BY,(ibh));

    }
    public void setBeingHeldByClientSync(int id) {
        dataTracker.set(HELD_BY_CLIENT_SYNC,(id));

    }


    @Override
    public boolean isPushable() {
        return !isLocked();
    }

    @Override
    public boolean isCollidable() {
        return super.isCollidable();//true;
    }

    public float getDegreeToBlow(){
        return -((float)(MathHelper.sin(((float)((getSwingingProgress())*Math.PI*2.0f)))*(Math.PI/4.0f)))/(MathHelper.PI*2)*360.0f;
    }
    boolean checkLocked(PlayerEntity player){
        if(isLocked())
        {
            player.sendMessage(Text.translatable("bigfanofit.fan.isLocked", this.getDisplayName()), true);
            player.playSound(Bigfan.FAN_LOCKED_STATUS_SNDEVT, this.getSoundCategory(), 0.4F, 1.0F);
            return true;
        }
        return false;
    }
    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {

        // TODO: LIFT UP FAN
        if(player.getMainHandStack().getItem() == Items.IRON_INGOT)
        {
            if(player instanceof ServerPlayerEntity serv && (serv.interactionManager.getGameMode() != GameMode.SPECTATOR && serv.interactionManager.getGameMode() != GameMode.ADVENTURE))
            {

                this.setLocked(!this.isLocked());
                this.playSound(this.isLocked()?Bigfan.FAN_LOCKED_SNDEVT:Bigfan.FAN_UNLOCKED_SNDEVT, 0.4f,isLocked()?0.5f:1.5f);
                player.sendMessage(Text.translatable(isLocked()?"bigfanofit.fan.isNowLocked":"bigfanofit.fan.isNowUnlocked", this.getDisplayName()), true);
            }
            return ActionResult.SUCCESS;
        }
        if(player instanceof ServerPlayerEntity s && checkLocked(s))
        {
            return ActionResult.CONSUME;
        }
        else if(player.world.isClient())
        {
            if(player instanceof ClientPlayerEntity s &&isLocked())
            {
                return ActionResult.CONSUME;
            }
        }

        if(player.getMainHandStack().isEmpty())
        {
            if(player.isSneaking())
            {
                setSwinging(!isSwinging());

                this.playSound(isSwinging()?Bigfan.FAN_SWING_SNDEVT:Bigfan.FAN_UNSWING_SNDEVT, 0.4f,0.5f);
            }
            else
                scrollFanNumber(1);
            return ActionResult.SUCCESS;
        }
        else



        return super.interact(player, hand);
    }


    public boolean damage(DamageSource source, float amount) {

        if (!this.world.isClient && !this.isRemoved()) {

            if (this.isInvulnerableTo(source)) {
                return false;
            } else {
                //boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).getAbilities().creativeMode;
                if (getHitTimer() > 20 && !this.isLocked()) {
                    this.removeAllPassengers();
                    if (source.getAttacker()!=null && source.getAttacker() instanceof PlayerEntity atp && atp.getAbilities().creativeMode) {
                        this.discard();
                    } else {
                        this.dropItems(source);
                        this.discard();
                    }
                }
                else
                {
                    if(source.getAttacker()!=null)
                    {


                        if((source.getAttacker().isSneaking() && (source.getSource()!=null && source.getSource() instanceof PlayerEntity)) || (source.getSource()!=null && source.getSource() instanceof EnderPearlEntity)){
                            if(checkLocked((PlayerEntity) source.getAttacker()))
                            {
                                return false;
                            }
                            if(!isBeingHeld())
                            {
                                setBeingHeld(true);
                                setBeingHeldBy(Optional.of(source.getAttacker().getUuid()));
                                setBeingHeldByClientSync(source.getAttacker().getId());
                            }
                            else
                            {
                                setBeingHeld(false);
                                this.setVelocity(0,0,0);
                                this.velocityModified = true;


                            }
                        }
                        if(!this.isLocked()) setHitTimer(getHitTimer()+9);
                    }
                    if(!this.isLocked()) setHitTimer(getHitTimer()+2);
                }

                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean canUsePortals() {
        return !this.isBeingHeld() && super.canUsePortals();
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
        getDataTracker().set(LOCKED,nbt.getBoolean("Locked"));
        getDataTracker().set(IS_BEING_HELD,nbt.getBoolean("isBeingHeld"));
        try{
            getDataTracker().set(HELD_BY,Optional.of(nbt.getUuid("HeldBy")));
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {

        nbt.putBoolean("Swinging",getDataTracker().get(SWINGING));
        nbt.putInt("FanPower",getDataTracker().get(FAN_POWER));
        nbt.putFloat("FanSwingSpeed",getDataTracker().get(FAN_SWING_SPEED));
        nbt.putFloat("SwingProgress",getDataTracker().get(SWING_PROGRESS));
        nbt.putFloat("FanRotationSpeed",getDataTracker().get(FAN_BLADE_ROTATION_SPEED));
        nbt.putFloat("FanRotation",getDataTracker().get(FAN_BLADE_ROTATION));
        nbt.putFloat("FanRotationAccel",getDataTracker().get(FAN_BLADE_ROTATION_ACCELERATION));
        nbt.putBoolean("isBeingHeld",getDataTracker().get(IS_BEING_HELD));
        nbt.putBoolean("Locked",getDataTracker().get(LOCKED));
        try{
            if(getDataTracker().get(HELD_BY)!=null)
                nbt.putUuid("HeldBy",getDataTracker().get(HELD_BY).isPresent()?getDataTracker().get(HELD_BY).get():null);
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        if (!this.world.isClient()) {
            this.setPosition(x, y, z);

            if (!this.hasPassengers()) {
                this.setRotation(yaw, pitch);
            }
        } else {
            this.targetX = x;
            this.targetY = y;
            this.targetZ = z;
            this.targetTicks = this.getType().getTrackTickInterval();
        }
    }

    private void positionTrackTick() {
        if (this.targetTicks > 0) {
            this.setPosition(
                    this.getX() + (this.targetX - this.getX()) / (double)this.targetTicks,
                    this.getY() + (this.targetY - this.getY()) / (double)this.targetTicks,
                    this.getZ() + (this.targetZ - this.getZ()) / (double)this.targetTicks
            );

            this.targetTicks--;
        }
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
            case 1: setRotationAcceleration(0.03f); rotSpeedLimit = 2f; break;
            case 2: setRotationAcceleration(0.04f); rotSpeedLimit = 4.5f;break;
            case 3: setRotationAcceleration(0.05f); rotSpeedLimit = 7f;break;
            case 4: setRotationAcceleration(0.08f); rotSpeedLimit = 10f; break;
        }
        if(getFanPower()!=0) setRotationSpeed(MathHelper.clamp(getRotationSpeed()+getRotationAcceleration(),-rotSpeedLimit,rotSpeedLimit));
        setBladeRotation(getBladeRotation()+MathHelper.clamp(getRotationSpeed(),-rotSpeedLimit,rotSpeedLimit));


        //this.setVelocity(this.getVelocity().multiply(0.98));

        //System.out.println((world.isClient?"client":"server")+" has " + " " +getPos());

        if(world.isClient)
        {

            this.prevX = this.getX();
            this.prevY = this.getY();
            this.prevZ = this.getZ();
            this.positionTrackTick();
        }

        if(isBeingHeld())
        {
            Entity ent = null;
            if(!this.world.isClient)
            {
                ServerWorld srvw = (ServerWorld) world;
                if(srvw.getEntity(getBeingHeldBy())!=null){
                    ent = srvw.getEntity(getBeingHeldBy());
                    setBeingHeldByClientSync(ent.getId());
                }

            }
            ent = world.getEntityById(getBeingHeldByClientSync());
            if(ent!=null)
            {
                Vec3d looking = ent.getRotationVec(0f).normalize();
                this.setPosition(ent.getX()+looking.getX(),ent.getY()+ent.getEyeHeight(ent.getPose())+looking.getY()-this.getHeight()/2.0f,ent.getZ()+looking.getZ());

                this.setYaw(ent.getYaw());
                this.setPitch(ent.getPitch());
                this.velocityModified = true;
                if(!world.isClient)
                {

                    this.updatePositionAndAngles(this.getX(),this.getY(),this.getZ(),getYaw(),getPitch());

                    this.updateTrackedPositionAndAngles(this.getX(),this.getY(),this.getZ(),getYaw(),getPitch(),1,true);

                }





                //System.out.println((world.isClient?"client":"server")+ this.getPos());
            }
            if(ent instanceof LivingEntity le && le.isDead())
            {
                this.setBeingHeldBy(Optional.empty());
                this.setBeingHeld(false);
                setBeingHeldByClientSync(-1);
            }
        }
        else
        {

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
        }

        //this.checkBlockCollision();

        updatePositionAndAngles(this.getX(),this.getY(),this.getZ(),this.getYaw(),this.getPitch());
        prevSwingProg = getSwingingProgress();
        if( this.getFanPower()>0 && this.isSwinging())
        {
            this.setSwingingProgress(this.getSwingingProgress()+this.getSwingSpeed());
        }
        Vec3d blowFrom = getPos();
        blowFrom = blowFrom.add(Vec3d.fromPolar(this.getPitch(),getYaw()+getDegreeToBlow()).normalize().multiply(-3/16f));
        //blowFrom.add(0f,0.75f,0f);
        Vec3d blowWay = Vec3d.fromPolar(this.getPitch(),getYaw()+getDegreeToBlow()).normalize().multiply((getRotationSpeed()/20f));


        Optional<BlockPos> lavenderInterop = BlockPos.findClosest(this.getBlockPos(),3,3,blockPos -> {
            return Bigfan.LAVENDER_CHECKER.get().test(world.getBlockState(blockPos).getBlock());
        });

        boolean isLavenderNearby = lavenderInterop.isPresent();
        if(getHitTimer()>0)setHitTimer(getHitTimer()-1);
        //System.out.println(getDegreeToBlow());
        age++;
        if(MathHelper.abs(getRotationSpeed())>0)
        {

            if(Math.round(MathHelper.abs(getRotationSpeed()/1f))>0f && (this.age%(7-getFanPower())==0))
            playSound(Bigfan.FAN_HUMS_SOUND_EVENT,0.4f,Math.abs(getRotationSpeed())/12.0f);
            if(isBeingHeld())
            {

                if(!this.world.isClient)
                {
                    if(isBeingHeld())
                    {
                        ServerWorld srvw = (ServerWorld) world;
                        if(srvw.getEntity(getBeingHeldBy())!=null){
                            Entity ent = srvw.getEntity(getBeingHeldBy());
                            if(!ent.isSneaking() && (ent instanceof PlayerEntity && !((PlayerEntity) ent).getAbilities().flying))
                            {

                                //Vec3d addVel = ent.getRotationVec(0).normalize().multiply(getRotationSpeed()/180.0f);

                                Vec3d addVel = Vec3d.fromPolar(ent.getPitch(),ent.getYaw()+getDegreeToBlow()).normalize().multiply((getRotationSpeed()/60f));
                                addVel = addVel.multiply(-1);
                                ent.setVelocity(ent.getVelocity().add(addVel).getX(),ent.getVelocity().add(addVel).getY(),ent.getVelocity().add(addVel).getZ());
                                ent.velocityModified = true;
                                ent.updatePosition(ent.getX(),ent.getY(),ent.getZ());
                                if(ent instanceof LivingEntity){
                                    LivingEntity target = (LivingEntity) ent;
                                    if (target instanceof ServerPlayerEntity && target.velocityModified) {
                                        ((ServerPlayerEntity)target).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
                                        target.velocityModified = false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            FanWindEntity wind = new FanWindEntity(Bigfan.FAN_WIND_ENTITY,world);
            wind.setFanOwner(this.getId());
            wind.setFanRotSpd(getRotationSpeed());
            wind.setPosition(blowFrom.add(0f,0.75f,0f).add(blowWay.multiply(-0.75)));
            if(isLavenderNearby)
            wind.setSpecialParticle(1);
            if(this.isOnFire()) wind.setWindOnFire(true);
            wind.setFanPowerLevel(this.getFanPower());
            if(isBeingHeld()) wind.setFanHolder(getBeingHeldBy());
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
