package xyz.aikoyori.bigfan.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.particle.CloudParticle;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import xyz.aikoyori.bigfan.Bigfan;
import xyz.aikoyori.bigfan.entities.FanEntityModel;
import xyz.aikoyori.bigfan.entities.FanEntityRenderer;
import xyz.aikoyori.bigfan.particles.LeafParticles;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class BigfanClient implements ClientModInitializer {
    public static final EntityModelLayer FAN_LAYER = new EntityModelLayer(new Identifier(Bigfan.MOD_ID, "fan"), "fan");
    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.register(Bigfan.FAN_ENTITY,ctx -> {
return new FanEntityRenderer(ctx,new FanEntityModel(ctx.getPart(FAN_LAYER)),0.5f);
        });
        EntityModelLayerRegistry.registerModelLayer(FAN_LAYER, FanEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(Bigfan.FAN_WIND_ENTITY, EmptyEntityRenderer::new);
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
            registry.register(new Identifier(Bigfan.MOD_ID, "particle/leaf_blow"));
        }));

        ParticleFactoryRegistry.getInstance().register(Bigfan.LEAF_BLOW, LeafParticles.LeafFactory::new);
        ParticleFactoryRegistry.getInstance().register(Bigfan.LAVENDER_BLOW, LeafParticles.LavenderPetalsParticle::new);
    }
}
