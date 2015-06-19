package corobot.util;

import java.util.List;

import javax.vecmath.Vector3f;

import com.corosus.ai.AIBTAgent;
import com.corosus.entity.IEntity;
import com.corosus.world.IWorld;

import corobot.Corobot;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class UtilEnt {

	public static EntityItem getClosestItem(World world, Vector3f pos, Item item) {
		List<Object> listItems = world.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(pos.x, pos.y, pos.z, pos.x, pos.y, pos.z).expand(10, 5, 10));
		double closestDist = 9999;
		EntityItem closestItem = null; 
		for (Object obj : listItems) {
			EntityItem itemEnt = (EntityItem) obj;
			if (itemEnt.getEntityItem().getItem() == item) {
				double distToItem = itemEnt.getDistanceSq(pos.x, pos.y, pos.z);
				if (distToItem < closestDist) {
					closestDist = distToItem;
					closestItem = itemEnt;
				}
			}
		}
		return closestItem;
	}
	
	public static void faceEntity(Entity entToRotate, Entity par1Entity, float par2, float par3)
    {
        double d0 = par1Entity.posX - entToRotate.posX;
        double d1 = par1Entity.posZ - entToRotate.posZ;
        double d2;

        if (par1Entity instanceof EntityLivingBase)
        {
        	EntityLivingBase entityliving = (EntityLivingBase)par1Entity;
            d2 = entityliving.posY + (double)entityliving.getEyeHeight() - (entToRotate.posY + (double)entToRotate.getEyeHeight());
        }
        else
        {
            d2 = (par1Entity.boundingBox.minY + par1Entity.boundingBox.maxY) / 2.0D - (entToRotate.posY + (double)entToRotate.getEyeHeight());
        }

        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1);
        float f2 = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
        float f3 = (float)(-(Math.atan2(d2, d3) * 180.0D / Math.PI));
        entToRotate.rotationPitch = updateRotation(entToRotate.rotationPitch, f3, par3);
        entToRotate.rotationYaw = updateRotation(entToRotate.rotationYaw, f2, par2);
    }
	
	public static void facePos(Entity entToRotate, Vector3f pos, float par2, float par3)
    {
        double d0 = pos.x - entToRotate.posX;
        double d1 = pos.z - entToRotate.posZ;
        double d2 = pos.y - entToRotate.posY + (double)entToRotate.getEyeHeight();

        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1);
        float f2 = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
        float f3 = (float)(-(Math.atan2(d2, d3) * 180.0D / Math.PI));
        entToRotate.rotationPitch = updateRotation(entToRotate.rotationPitch, f3, par3);
        entToRotate.rotationYaw = updateRotation(entToRotate.rotationYaw, f2, par2);
    }
	
	public static float updateRotation(float par1, float par2, float par3)
    {
        float f3 = MathHelper.wrapAngleTo180_float(par2 - par1);

        if (f3 > par3)
        {
            f3 = par3;
        }

        if (f3 < -par3)
        {
            f3 = -par3;
        }

        return par1 + f3;
    }
	
	public static boolean canSeeCoord(Vector3f posFrom, Vector3f posTo) {
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		EntityPlayer playerEnt = Corobot.getPlayerAI().bridgePlayer.getPlayer();
		World worldMC = Minecraft.getMinecraft().theWorld;
		
		Vec3 vecFrom = Vec3.createVectorHelper(posFrom.x, posFrom.y, posFrom.z);
		Vec3 vecTo = Vec3.createVectorHelper(posTo.x, posTo.y, posTo.z);
		
		MovingObjectPosition pos = worldMC.rayTraceBlocks(vecFrom, vecTo);
		
		//return true if no hit, or the hit was right infront of the block we are targetting
		if (pos == null) {
			return true;
		} else if (pos.hitVec.distanceTo(vecTo) <= 1) {
			System.out.println("canSee hit dist: " + pos.hitVec.distanceTo(vecTo));
			return true;
		} else {
			return false;
		}
	}
	
}
