package xyz.aikoyori.bigfan.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class LeafParticles extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    private float rotateSpeed = 0f;
    double ogScale;
    Random rand = new Random();
    protected LeafParticles(ClientWorld clientWorld, double d, double e, double f, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider,boolean tint) {
        super(clientWorld, d, e, f, 0.0, 0.0, 0.0);
        this.velocityMultiplier = 0.96F;
        this.spriteProvider = spriteProvider;
        this.velocityX *= 0.10000000149011612;
        this.velocityY *= 0.10000000149011612;
        this.velocityZ *= 0.10000000149011612;
        this.velocityX += velocityX;
        this.velocityY += velocityY;
        this.velocityZ += velocityZ;
        this.setSprite(spriteProvider);
        //float g = 1.0F - (float)(Math.random() * 0.30000001192092896);

        if(tint)
        {
            int col = clientWorld.getBiome(new BlockPos(d,e,f)).value().getFoliageColor();
            this.red = ColorHelper.Argb.getRed(col)/256.0f;
            this.green = ColorHelper.Argb.getGreen(col)/256.0f;
            this.blue = ColorHelper.Argb.getBlue(col)/256.0f;
        }
        this.scale *= Math.random() * 0.8 + 0.7;
        ogScale = scale;
        int i = (int)(8.0 / (Math.random() * 0.8 + 0.3));
        this.maxAge = (int)Math.max((float)i * 2.5F, 1.0F);
        this.collidesWithWorld = true;
        this.angle = (float) (Math.random()*MathHelper.PI)-MathHelper.HALF_PI;
        this.rotateSpeed = (rand.nextBoolean()?-1:1)*(float) ((Math.random()*12.0f)+1.2f);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        this.prevAngle = this.angle;


        if (this.age++ >= this.maxAge || this.world.getFluidState(new BlockPos(x,y,z)).getFluid().matchesType(Fluids.LAVA)) {
            this.scale = 0;
            this.markDead();
        }else {
            this.velocityY -= 0.04 * (double)this.gravityStrength;
            this.move(this.velocityX, this.velocityY, this.velocityZ);
            if (this.field_28787 && this.y == this.prevPosY) {
                this.velocityX *= 1.1;
                this.velocityZ *= 1.1;
            }

            this.velocityX *= (double)this.velocityMultiplier;
            this.velocityY *= (double)this.velocityMultiplier;
            this.velocityZ *= (double)this.velocityMultiplier;
            if (this.onGround) {
                this.velocityX *= 0.699999988079071;
                this.velocityZ *= 0.699999988079071;
            }

        }

        if (!this.dead) {
            if(age/((float)(maxAge))>3/4.0)
            {
                //this.scale = (float) (ogScale*((maxAge-age)/(maxAge*1f)*4));
                this.scale = this.scale*0.8f;
            }
            this.angle += ((float) (rotateSpeed* MathHelper.PI/360.0f));
        }

    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class LeafFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public LeafFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new LeafParticles(clientWorld, d, e, f, g, h, i, this.spriteProvider,true);
        }
    }
    @Environment(EnvType.CLIENT)
    public static class LavenderPetalsParticle implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public LavenderPetalsParticle(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new LeafParticles(clientWorld, d, e, f, g, h, i, this.spriteProvider,false);
        }
    }
}
