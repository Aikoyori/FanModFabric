package xyz.aikoyori.bigfan;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.aikoyori.bigfan.entities.FanEntity;
import xyz.aikoyori.bigfan.entities.FanWindEntity;
import xyz.aikoyori.bigfan.item.FanItem;

public class Bigfan implements ModInitializer {
    public static final String MOD_ID = "bigfanofit";
    public static final EntityType<FanEntity> FAN_ENTITY = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(MOD_ID,"fan"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, FanEntity::new).dimensions(EntityDimensions.fixed(14/16.0f,15/16.0f)).build());


    public static final EntityType<FanWindEntity> FAN_WIND_ENTITY = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(MOD_ID,"fan_wind"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, FanWindEntity::new).dimensions(EntityDimensions.fixed(0.25f,0.25f)).build());

    public static final DefaultParticleType LEAF_BLOW = FabricParticleTypes.simple();

    public static FanItem FAN_ITEM = new FanItem(new FabricItemSettings().maxCount(1).group(ItemGroup.TRANSPORTATION));

    public static final Identifier FAN_HUMS_ID = new Identifier(MOD_ID,"fan_hums");
    public static SoundEvent FAN_HUMS_SOUND_EVENT = new SoundEvent(FAN_HUMS_ID);
    public static final Identifier FAN_BUTTON_CLICK_ID = new Identifier(MOD_ID,"fan_button_click");
    public static SoundEvent FAN_BUTTON_SNDEVT = new SoundEvent(FAN_BUTTON_CLICK_ID);
    public static final Identifier FAN_BUTTON_SWITCH = new Identifier(MOD_ID,"fan_button_switch");
    public static SoundEvent FAN_BUTTON_SWITCH_EVT = new SoundEvent(FAN_BUTTON_SWITCH);
    public static final Identifier FAN_POWEROFF_ID = new Identifier(MOD_ID,"fan_poweroff");
    public static SoundEvent FAN_POWEROFF_SNDEVT = new SoundEvent(FAN_POWEROFF_ID);
    public static final Identifier FAN_SWING_CLICK_ID = new Identifier(MOD_ID,"fan_swing_click");
    public static SoundEvent FAN_SWING_SNDEVT = new SoundEvent(FAN_SWING_CLICK_ID);
    public static final Identifier FAN_UNSWING_CLICK_ID = new Identifier(MOD_ID,"fan_unswing_click");
    public static SoundEvent FAN_UNSWING_SNDEVT = new SoundEvent(FAN_UNSWING_CLICK_ID);

    @Override
    public void onInitialize() {

        Registry.register(Registry.PARTICLE_TYPE, new Identifier(MOD_ID, "leaf_blow"), LEAF_BLOW);
        Registry.register(Registry.ITEM,new Identifier(MOD_ID,"fan"),FAN_ITEM);
        Registry.register(Registry.SOUND_EVENT, FAN_HUMS_ID, FAN_HUMS_SOUND_EVENT);
        Registry.register(Registry.SOUND_EVENT, FAN_BUTTON_CLICK_ID, FAN_BUTTON_SNDEVT);
        Registry.register(Registry.SOUND_EVENT, FAN_SWING_CLICK_ID, FAN_SWING_SNDEVT);
        Registry.register(Registry.SOUND_EVENT, FAN_BUTTON_SWITCH, FAN_BUTTON_SWITCH_EVT);
        Registry.register(Registry.SOUND_EVENT, FAN_UNSWING_CLICK_ID, FAN_UNSWING_SNDEVT);
        Registry.register(Registry.SOUND_EVENT, FAN_POWEROFF_ID, FAN_POWEROFF_SNDEVT);

    }
}
