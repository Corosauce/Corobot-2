package corobot.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class UtilInventory {

	public static int getItemCount(IInventory inv, ItemStack itemToFind) {
		int curCount = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (isSame(stack, itemToFind)) {
			//if (stack != null && stack.getItem() == itemToFind.getItem() && (stack.getItemDamage() == itemToFind.getItemDamage())) {
				curCount += stack.stackSize;
			}
		}
		return curCount;
	}
	
	//mainly for recipe use, since it uses wildcard
	public static boolean isSame(ItemStack stack1, ItemStack stack2) {
		if (stack1 == null || stack2 == null) return false;
		//why do we do stacklimit == stacklimit, that should only be fore knowing if we need to compare meta
		//return stack1.getItem() == stack2.getItem() && ((stack1.getItem().getItemStackLimit(stack1) == 1 && stack2.getItem().getItemStackLimit(stack2) == 1) || stack1.getItemDamage() == stack2.getItemDamage() || stack1.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE);
		return stack1.getItem() == stack2.getItem() && 
				((!stack1.getItem().getHasSubtypes() && !stack2.getItem().getHasSubtypes()) || 
						(stack1.getItem().getItemStackLimit(stack1) != 1 && stack2.getItem().getItemStackLimit(stack2) != 1) || 
						(stack1.getItemDamage() == stack2.getItemDamage() || stack1.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE));
	}
	
}
