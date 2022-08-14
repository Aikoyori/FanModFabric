package xyz.aikoyori.bigfan;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
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
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, FanEntity::new).dimensions(EntityDimensions.fixed(14/16.0f,19/16.0f)).build());


    public static final EntityType<FanWindEntity> FAN_WIND_ENTITY = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(MOD_ID,"fan_wind"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, FanWindEntity::new).dimensions(EntityDimensions.fixed(0.1f,0.1f)).build());


    public static FanItem FAN_ITEM = new FanItem(new FabricItemSettings().maxCount(1).group(ItemGroup.TRANSPORTATION));
    @Override
    public void onInitialize() {

        Registry.register(Registry.ITEM,new Identifier(MOD_ID,"fan"),FAN_ITEM);
    }
}
