package corobot.util;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import corobot.ai.PlayerAI;

public class UtilPlayer {

	//returns best weapon slot, if not on hotbar, should probably transfer it to one
	//source could be from own inventory or a source chest
	public static void updateBestWeaponSlot(EntityPlayer player, IInventory sourceInventory, InventoryInfo invInfo) {
		
		float bestDamage = 0;
		int bestSlot = -1;
		
		ItemStack itemstack = player.inventory.getCurrentItem();
		if (itemstack != null) player.getAttributeMap().removeAttributeModifiers(itemstack.getAttributeModifiers());
		
		//System.out.println("FIND BEST WEAPON START");
		
		for (int i = 0; i < sourceInventory.getSizeInventory(); i++) {
			ItemStack slotStack = sourceInventory.getStackInSlot(i);
			
			
			
			//check what hostile worlds does for getting attribute, does it factor in tool damage?
			if (slotStack != null) {
				player.getAttributeMap().applyAttributeModifiers(slotStack.getAttributeModifiers());
				
				float baseDamage = (float)player.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
	            float enchantDamage = 0.0F;
	
	            if (player instanceof EntityLivingBase)
	            {
	            	//these need to have a target entity passed to them, hmmmmmmm, use own reference for now like old code apparently did
	            	enchantDamage = EnchantmentHelper.getEnchantmentModifierLiving(player, (EntityLivingBase)player);
	            }
	            
				float curDmg = baseDamage + enchantDamage;
			
				player.getAttributeMap().removeAttributeModifiers(slotStack.getAttributeModifiers());
				
				if (curDmg > bestDamage) {
					bestDamage = curDmg;
					bestSlot = i;
				}
				
				//System.out.println(i + ": " + slotStack + ": " + curDmg);
			}
		}
		
		//test
		//System.out.println("best slot: " + bestSlot + " - dmg: " + bestDamage);
		
		invInfo.bestMeleeSlot = bestSlot;
		invInfo.bestMeleeDamage = bestDamage;
	}
	
	public static int getBestToolSlot(Class toolClass, EntityPlayer player, IInventory sourceInventory) {
		float bestDamage = 0;
		int bestSlot = -1;
		
		ItemStack itemstack = player.inventory.getCurrentItem();
		//if (itemstack != null) player.getAttributeMap().removeAttributeModifiers(itemstack.getAttributeModifiers());
		
		//System.out.println("FIND BEST WEAPON START");
		
		for (int i = 0; i < sourceInventory.getSizeInventory(); i++) {
			ItemStack slotStack = sourceInventory.getStackInSlot(i);
			
			if (slotStack != null) {
				
				if (toolClass.isAssignableFrom(slotStack.getItem().getClass()) && slotStack.getItem() instanceof ItemTool) {
					ItemTool tool = (ItemTool) slotStack.getItem();
					
					float speed = tool.func_150913_i().getEfficiencyOnProperMaterial();
					
					if (speed > bestDamage) {
						bestDamage = speed;
						bestSlot = i;
					}
				}
			}
		}
		
		//test
		//System.out.println("best slot: " + bestSlot + " - dmg: " + bestDamage);
		
		return bestSlot;
		//invInfo.bestMeleeSlot = bestSlot;
		//invInfo.bestMeleeDamage = bestDamage;
	}
	
	public static int getSlotForItem(ItemStack stack, EntityPlayer player, IInventory sourceInventory, boolean hotBarOnly) {
		for (int i = 0; i < (hotBarOnly ? 10 : sourceInventory.getSizeInventory()); i++) {
			ItemStack slotStack = sourceInventory.getStackInSlot(i);
			
			if (slotStack != null) {
				if (slotStack.getItem() == stack.getItem() && slotStack.stackSize > 0) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public static int getBestFoodSlot(EntityPlayer player, IInventory sourceInventory) {
		return getBestFoodSlot(player, sourceInventory, false);
	}
	
	public static int getBestFoodSlot(EntityPlayer player, IInventory sourceInventory, boolean hotBarOnly) {
		float bestDamage = 0;
		int bestSlot = -1;
		
		ItemStack itemstack = player.inventory.getCurrentItem();
		if (itemstack != null) player.getAttributeMap().removeAttributeModifiers(itemstack.getAttributeModifiers());
		
		//System.out.println("FIND BEST WEAPON START");
		
		for (int i = 0; i < (hotBarOnly ? 10 : sourceInventory.getSizeInventory()); i++) {
			ItemStack slotStack = sourceInventory.getStackInSlot(i);
			
			if (slotStack != null) {
				
				if (slotStack.getItem() instanceof ItemFood && slotStack.getItem() != Items.rotten_flesh) {
					ItemFood tool = (ItemFood) slotStack.getItem();
					
					float speed = tool.func_150905_g(slotStack);
					
					if (speed > bestDamage) {
						bestDamage = speed;
						bestSlot = i;
					}
				}
			}
		}
		
		//test
		//System.out.println("best slot: " + bestSlot + " - dmg: " + bestDamage);
		
		return bestSlot;
		//invInfo.bestMeleeSlot = bestSlot;
		//invInfo.bestMeleeDamage = bestDamage;
	}
	
	public static void switchToMeleeSlot(PlayerAI playerAI) {
		playerAI.bridgePlayer.getPlayer().inventory.currentItem = playerAI.invInfo.bestMeleeSlot;
	}
	
	public static void switchToRangedSlot(PlayerAI playerAI) {
		if (playerAI.invInfo.bestRangedSlot == -1) return;
		playerAI.bridgePlayer.getPlayer().inventory.currentItem = playerAI.invInfo.bestRangedSlot;
	}
	
	public static void switchToFoodSlot(PlayerAI playerAI) {
		if (playerAI.invInfo.bestFoodSlot == -1) return;
		playerAI.bridgePlayer.getPlayer().inventory.currentItem = playerAI.invInfo.bestFoodSlot;
	}
	
	public static void optimizeOffensiveInventory() {
		//best attacking weapon in hotbar
		//c_AIP.i.mc.playerController.windowClick(windowID, bestSwordSlot, y, mouse2StepClick, c_AIP.i.mc.thePlayer);
		//c_AIP.i.mc.playerController.windowClick(windowID, myInvSlotStart+27+0, y, mouse2StepClick, c_AIP.i.mc.thePlayer);
	}
	
	public static void optimizeDefensiveInventory() {
		//armor org, what else
	}
	
}
