package corobot.util;

import com.corosus.util.UtilDbg;

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
	
	//mainly for recipe use, since it uses wildcard, doesnt compare stacksize if stacksize is actually used
	public static boolean isSame(ItemStack stack1, ItemStack stack2) {
		if (stack1 == null || stack2 == null) return false;
		if (stack1.getItem() == null || stack2.getItem() == null) {
			//UtilDbg.out("WARNING! plan has a null item! or we are at least comparing against a null item somewhere!");
			return false;
		}
		//why do we do stacklimit == stacklimit, that should only be fore knowing if we need to compare meta
		//return stack1.getItem() == stack2.getItem() && ((stack1.getItem().getItemStackLimit(stack1) == 1 && stack2.getItem().getItemStackLimit(stack2) == 1) || stack1.getItemDamage() == stack2.getItemDamage() || stack1.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE);
		return stack1.getItem() == stack2.getItem() && 
				((!stack1.getItem().getHasSubtypes() && !stack2.getItem().getHasSubtypes()) || 
						(stack1.getItem().getItemStackLimit(stack1) != 1 && stack2.getItem().getItemStackLimit(stack2) != 1) || 
						(stack1.getItemDamage() == stack2.getItemDamage() || stack1.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE));
	}
	
}
