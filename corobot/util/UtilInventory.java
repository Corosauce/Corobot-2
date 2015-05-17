package corobot.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class UtilInventory {

	public static int getItemCount(IInventory inv, Item itemToFind) {
		int curCount = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null && stack.getItem() == itemToFind) {
				curCount += stack.stackSize;
			}
		}
		return curCount;
	}
	
}
