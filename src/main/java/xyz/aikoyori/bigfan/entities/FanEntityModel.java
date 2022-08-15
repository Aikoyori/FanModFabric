package xyz.aikoyori.bigfan.entities;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

// Made with Blockbench 4.3.1
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class FanEntityModel extends EntityModel<Entity> {
	private final ModelPart fan;
	public FanEntityModel(ModelPart root) {
		this.fan = root.getChild("fan");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData fan = modelPartData.addChild("fan", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -1.0F, -7.5F, 12.0F, 1.0F, 11.0F, new Dilation(0.0F))
				.uv(34, 40).cuboid(-1.5F, -0.75F, -1.0F, 3.0F, 2.0F, 3.0F, new Dilation(0.0F))
				.uv(16, 40).cuboid(-1.0F, -0.75F, -0.5F, 2.0F, 10.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.0F, 3.5F));

		ModelPartData baseslant_r1 = fan.addChild("baseslant_r1", ModelPartBuilder.create().uv(28, 20).cuboid(-4.5F, -1.0F, -3.1F, 9.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5F, -1.5F, 0.0873F, 0.0F, 0.0F));

		ModelPartData powersbutton = fan.addChild("powersbutton", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.25F, -5.0F, 0.0873F, 0.0F, 0.0F));

		ModelPartData button0 = powersbutton.addChild("button4", ModelPartBuilder.create().uv(28, 30).cuboid(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, -0.25F, 0.5F));

		ModelPartData button1 = powersbutton.addChild("button3", ModelPartBuilder.create().uv(28, 28).cuboid(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.5F, -0.25F, 0.5F));

		ModelPartData button2 = powersbutton.addChild("button2", ModelPartBuilder.create().uv(28, 25).cuboid(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.25F, 0.5F));

		ModelPartData button3 = powersbutton.addChild("button1", ModelPartBuilder.create().uv(28, 23).cuboid(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(1.5F, -0.25F, 0.5F));

		ModelPartData button4 = powersbutton.addChild("button0", ModelPartBuilder.create().uv(6, 7).cuboid(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, -0.25F, 0.5F));

		ModelPartData twisty = fan.addChild("twisty", ModelPartBuilder.create().uv(28, 28).cuboid(-2.0F, 0.0F, -5.5F, 4.0F, 4.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 9.0F, 1.5F));

		ModelPartData bone = twisty.addChild("bone", ModelPartBuilder.create().uv(28, 20).cuboid(-0.5F, 12.0F, 2.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -9.0F, -1.5F));

		ModelPartData face = twisty.addChild("face", ModelPartBuilder.create().uv(0, 7).cuboid(-1.0F, 9.75F, -6.5F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -8.75F, -1.5F));

		ModelPartData grills = face.addChild("grills", ModelPartBuilder.create().uv(0, 26).cuboid(-7.0F, -7.0F, -2.0F, 14.0F, 14.0F, 0.0F, new Dilation(0.0F))
				.uv(0, 12).cuboid(-7.0F, -7.0F, 2.0F, 14.0F, 14.0F, 0.0F, new Dilation(0.0F))
				.uv(8, 36).cuboid(-7.0F, -7.0F, -2.0F, 0.0F, 14.0F, 4.0F, new Dilation(0.0F))
				.uv(0, 36).cuboid(7.0F, -7.0F, -2.0F, 0.0F, 14.0F, 4.0F, new Dilation(0.0F))
				.uv(24, 16).cuboid(-7.0F, 7.0F, -2.0F, 14.0F, 0.0F, 4.0F, new Dilation(0.0F))
				.uv(24, 12).cuboid(-7.0F, -7.0F, -2.0F, 14.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 10.75F, -5.0F));

		ModelPartData propeler = face.addChild("propeler", ModelPartBuilder.create().uv(43, 5).cuboid(-1.5F, -1.5F, -0.75F, 3.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 10.75F, -5.25F));

		ModelPartData blades = propeler.addChild("blades", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -2.75F));

		ModelPartData blade3_r1 = blades.addChild("blade3_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, 0.0F, 0.0F, 5.0F, 7.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 3.0F, 0.0F, 0.1745F, -2.0944F));

		ModelPartData blade2_r1 = blades.addChild("blade2_r1", ModelPartBuilder.create().uv(35, 0).cuboid(-4.0F, 0.0F, 0.0F, 5.0F, 7.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 3.0F, 0.0F, -0.1745F, 0.0F));

		ModelPartData blade1_r1 = blades.addChild("blade1_r1", ModelPartBuilder.create().uv(24, 40).cuboid(-4.0F, 0.0F, 0.0F, 5.0F, 7.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 3.0F, 0.0F, -0.1745F, 2.0944F));
		return TexturedModelData.of(modelData, 64, 64);
	}
	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}
	public void setSwingProgressForTwistingFace(float prevRotationProgress, float rotationProgress, float deltaTicks,float pitch) {
		fan.getChild("twisty").setAngles(pitch,(float)(MathHelper.sin(((float)(MathHelper.lerp(deltaTicks,prevRotationProgress,rotationProgress)*Math.PI*2.0f)))*(Math.PI/4.0f)),0f);
	}

	public void setPowerButtonPress(int powerLevel) {
		for(int i=0;i<5;i++)
		{
			setPowerButtonActive(i,i==powerLevel&&powerLevel!=0);
		}

	}
	private void setPowerButtonActive(int powerLevel,boolean active) {
		ModelPart mp = fan.getChild("powersbutton").getChild("button"+powerLevel);
		mp.setAngles(active?((-10f)*((float)Math.PI/180f)):0f,0f,0f);
	}
	public void setBladeRotation(float prevRot, float rot, float deltaTicks) {
		fan.getChild("twisty").getChild("face").getChild("propeler").setAngles(0f,0f,(float)(MathHelper.lerp(deltaTicks,prevRot,rot)));
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		matrices.push();
		fan.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		matrices.pop();
	}
}
