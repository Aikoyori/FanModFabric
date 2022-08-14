package xyz.aikoyori.bigfan.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import xyz.aikoyori.bigfan.Bigfan;
import xyz.aikoyori.bigfan.entities.FanEntity;

public class FanItem extends Item {
    public FanItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos positive = switch (context.getSide())
        {
            case DOWN:{
                yield new BlockPos(context.getBlockPos().getX(),context.getBlockPos().getY()-1,context.getBlockPos().getZ());
            }
            case UP:{
                yield new BlockPos(context.getBlockPos().getX(),context.getBlockPos().getY()+1,context.getBlockPos().getZ());
            }
            case NORTH:{
                yield new BlockPos(context.getBlockPos().getX(),context.getBlockPos().getY(),context.getBlockPos().getZ()-1);
            }
            case SOUTH: {
                yield new BlockPos(context.getBlockPos().getX(),context.getBlockPos().getY(),context.getBlockPos().getZ()+1);
            }
            case WEST: {
                yield new BlockPos(context.getBlockPos().getX()-1,context.getBlockPos().getY(),context.getBlockPos().getZ());
            }
            case EAST: {
                yield new BlockPos(context.getBlockPos().getX()+1,context.getBlockPos().getY(),context.getBlockPos().getZ());
            }
            default: {
                yield new BlockPos(context.getBlockPos().getX(),context.getBlockPos().getY(),context.getBlockPos().getZ());

            }
        };
        System.out.println(positive);
        System.out.println(context.getSide());
        World world = context.getWorld();
        Box box = Bigfan.FAN_ENTITY.getDimensions().getBoxAt(positive.getX()+0.5, positive.getY(), positive.getZ()+0.5);
        if (world.isSpaceEmpty((Entity)null, box) && world.getOtherEntities((Entity)null, box).isEmpty()) {

            FanEntity fan = new FanEntity(Bigfan.FAN_ENTITY,world);
            fan.setPos(positive.getX()+0.5,positive.getY()+0.1,positive.getZ()+0.5);
            fan.setYaw(context.getPlayerYaw()+(context.getPlayer().isSneaking()?0f:180f));
            context.getWorld().spawnEntity(fan);
            context.getPlayer().getStackInHand(context.getHand()).decrement(1);
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(context);
    }
}
