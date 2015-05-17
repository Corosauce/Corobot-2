package corobot.util;

import java.util.List;

import javax.vecmath.Vector3f;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
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
	
}
