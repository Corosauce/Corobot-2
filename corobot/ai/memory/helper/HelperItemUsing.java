package corobot.ai.memory.helper;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import corobot.Corobot;
import corobot.ai.memory.helper.HelperPath.Repaths;

public class HelperItemUsing {

	/**
	 * this class is also affected by guichat not being open
	 * 
	 * The purpose of this class is to prevent corobot from switching away from an item in use
	 * 
	 * Priorities probably need to come in to play here somehow
	 * 
	 * plan for safety in use timeout for edge cases
	 * 
	 * Items that require locking / charge use:
	 * - Food
	 * - Bow
	 * - Pickaxe?
	 * 
	 * Items that are insta use:
	 * - Sword
	 * - Hoe
	 * - 
	 */
	
	public enum ItemUse {
		NONE, FOOD, BOW, PICKAXE
	}
	
	public static HashMap<ItemUse, Long> lookupMaxUseTimes = new HashMap<ItemUse, Long>();
	
	static {
		lookupMaxUseTimes.put(ItemUse.NONE, 0L);
		lookupMaxUseTimes.put(ItemUse.FOOD, 2000L);
		lookupMaxUseTimes.put(ItemUse.BOW, 1500L);
		lookupMaxUseTimes.put(ItemUse.PICKAXE, 30000L); //should be dynamic
	}
	
	private static ItemUse itemInUse = ItemUse.NONE;
	//private static int ticksInUse = 0;
	private static long timeStartUse = 0;
	private static boolean sentUsePacket = false;
	
	public static boolean isUsingItem() {
		return itemInUse != ItemUse.NONE;
	}
	
	public static boolean isUsing(ItemUse val) {
		return itemInUse == val;
	}
	
	public static void setInUse(ItemUse val, int slot) {
		//Corobot.dbg("setInUse: " + val + " - " + slot);
		itemInUse = val;
		timeStartUse = System.currentTimeMillis();
		if (slot != -1) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayer player = Corobot.playerAI.bridgePlayer.getPlayer();
			player.inventory.currentItem = slot;
		}
	}
	
	public static void tickUsageUpdate() {
		lookupMaxUseTimes.put(ItemUse.FOOD, 2000L);
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = Corobot.playerAI.bridgePlayer.getPlayer();
		ItemStack stack = player.getCurrentEquippedItem();
		
		if (isUsingItem()) {
			//ticksInUse++;
			
			if (stack != null) {
				
				if (!sentUsePacket) {
					sentUsePacket = true;
					mc.playerController.sendUseItem(player, player.worldObj, stack);
				}
				
			}
			
			if (System.currentTimeMillis() - timeStartUse > lookupMaxUseTimes.get(itemInUse)) {
				mc.playerController.onStoppedUsingItem(player);
				endUsage();
			}
			
			
			
			
		}
	}
	
	public static void endUsage() {
		//Corobot.dbg("end item usage");
		itemInUse = ItemUse.NONE;
		sentUsePacket = false;
	}
	
}
