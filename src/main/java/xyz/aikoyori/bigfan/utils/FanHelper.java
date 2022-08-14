package xyz.aikoyori.bigfan.utils;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FanHelper {
    public static float[] Vec3dtoDegrees(Vec3d vecIn){

        float yaw = -(float) MathHelper.atan2(vecIn.x,vecIn.z);
        float pitch = -(float) Math.asin(-vecIn.y);
        return new float[]{(float) (yaw*180.0/MathHelper.PI),(float) (pitch*180.0/MathHelper.PI)};
    }
}
