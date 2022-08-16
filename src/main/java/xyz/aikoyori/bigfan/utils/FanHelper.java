package xyz.aikoyori.bigfan.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.function.Predicate;

import static net.minecraft.entity.projectile.ProjectileUtil.getEntityCollision;

public class FanHelper {
    public static float[] Vec3dtoDegrees(Vec3d vecIn){

        float yaw = -(float) MathHelper.atan2(vecIn.x,vecIn.z);
        float pitch = -(float) Math.asin(-vecIn.y);
        return new float[]{(float) (yaw*180.0/MathHelper.PI),(float) (pitch*180.0/MathHelper.PI)};
    }
    public static HitResult getOutlineCollision(Entity entity, Predicate<Entity> predicate) {
        Vec3d vec3d = entity.getVelocity();
        World world = entity.world;
        Vec3d vec3d2 = entity.getPos();
        Vec3d vec3d3 = vec3d2.add(vec3d);
        HitResult hitResult = world.raycast(new RaycastContext(vec3d2, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, entity));
        if (((HitResult)hitResult).getType() != HitResult.Type.MISS) {
            vec3d3 = ((HitResult)hitResult).getPos();
        }

        HitResult hitResult2 = getEntityCollision(world, entity, vec3d2, vec3d3, entity.getBoundingBox().stretch(entity.getVelocity()).expand(1.0), predicate);
        if (hitResult2 != null) {
            hitResult = hitResult2;
        }

        return (HitResult)hitResult;
    }
    public static HitResult getCollisionFromVector(Entity entity, Predicate<Entity> predicate,Vec3d vecIn) {

        World world = entity.world;
        Vec3d vec3d2 = entity.getPos();
        Vec3d vec3d3 = vec3d2.add(vecIn);
        HitResult hitResult = world.raycast(new RaycastContext(vec3d2, vec3d3, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
        if (((HitResult)hitResult).getType() != HitResult.Type.MISS) {
            vec3d3 = ((HitResult)hitResult).getPos();
        }

        HitResult hitResult2 = getEntityCollision(world, entity, vec3d2, vec3d3, entity.getBoundingBox().stretch(vecIn).expand(1.0), predicate);
        if (hitResult2 != null) {
            hitResult = hitResult2;
        }

        return (HitResult)hitResult;
    }

}
