package xyz.aikoyori.bigfan.entities;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import xyz.aikoyori.bigfan.Bigfan;

public class FanEntityRenderer extends EntityRenderer<FanEntity> {

    FanEntityModel entityModel;
    public static final Identifier TX = new Identifier(Bigfan.MOD_ID, "textures/entity/fantex.png");
    public FanEntityRenderer(EntityRendererFactory.Context ctx, FanEntityModel entityModele, float f) {
        super(ctx);
        entityModel = entityModele;
        shadowRadius = f;
    }

    @Override
    public Identifier getTexture(FanEntity entity) {
        return TX;
    }

    @Override
    public void render(FanEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();
        //RenderSystem.setShaderColor(1, 1, 1, 1);

        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-entity.getYaw(tickDelta)+180f));
        entityModel.setSwingProgressForTwistingFace(entity.getPrevSwingProg(), entity.getSwingingProgress(),tickDelta,-entity.getPitch()* MathHelper.PI/180.0f);
        entityModel.setBladeRotation(entity.getPrevBladeRot(),entity.getBladeRotation(),tickDelta);
        entityModel.setPowerButtonPress(entity.getFanPower());
        entityModel.render(matrices,vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(TX)),light, OverlayTexture.DEFAULT_UV,1.0f,1.0f,1.0f,1.0f);

        matrices.pop();
    }

    @Override
    public boolean shouldRender(FanEntity entity, Frustum frustum, double x, double y, double z) {
        //return true;
        return super.shouldRender(entity, frustum, x, y, z);
    }

}
