package corobot.ai.memory.pieces;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

import com.corosus.ai.minigoap.IWorldStateProperty;

import corobot.Corobot;
import corobot.ai.memory.pieces.inventory.InventorySource;
import corobot.util.UtilInventory;

public class ItemEntry implements IWorldStateProperty {

	private ItemStack stack;
	
	//should source be optional? maybe if null it means get it from anywhere / can go anywhere?
	//- so far that seems to make sense, it shouldnt be a factor in preconditions UNTIL we start weighing our best choices for plans
	private InventorySource source;
	
	private int reUseAmount = 0;
	
	public ItemEntry(ItemStack stack, InventorySource source) {
		if (stack.getItem() == null) {
			Corobot.dbg("warning! item null!");
		}
		this.stack = stack;
		this.source = source;
		
		//TODO: shouldnt this be contextual? what if a tool is needed as part of a crafting recipe and not for mining?
		if (stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemSword) {
			reUseAmount = 50;
		}
	}
	
	public ItemStack getStack() {
		return stack;
	}

	public void setStack(ItemStack stack) {
		this.stack = stack;
	}

	public InventorySource getSource() {
		return source;
	}
	
	public void setSource(InventorySource source) {
		this.source = source;
	}

	@Override
	public boolean canEffectSatisfyPrecondition(IWorldStateProperty precondition) {
		//TODO: factor in source of this item?
		//might only be needed to be considered after planning
		if (precondition instanceof ItemEntry) {
			ItemEntry precond = (ItemEntry) precondition;
			/*if (ItemStack.areItemStacksEqual(stack, precond.stack)) {
				System.out.println("old true");
			}*/
			/*if (ItemStack.areItemStacksEqual(stack, precond.stack)) {
				return true;
			} else {*/
				//if (stack.isItemEqual(precond.stack)) {
				//if (stack.getItem() == precond.stack.getItem() && (stack.getItemDamage() == precond.stack.getItemDamage() || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE || precond.stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)) {
				if (UtilInventory.isSame(stack, precond.stack)) {
					if (stack.getMaxStackSize() > 1 && precond.stack.getMaxStackSize() > 1) {
						if (stack.stackSize >= precond.stack.stackSize) {
							return true;
						}
					} else {
						return true;
					}
				}
			//}
			return false;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		if (stack.getItem() != null) {
			return stack + " - " + stack.getDisplayName();///* + ":" + stack.stackSize + ":" + stack.getItemDamage()*/ + " from " + source;
		} else {
			return "" + stack.getItem();///* + ":" + stack.stackSize + ":" + stack.getItemDamage()*/ + " from " + source;
		}
	}

	@Override
	public boolean isSame(IWorldStateProperty prop) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setAmount(int amount) {
		if (amount > 64) {
			Corobot.dbg("WARNING: amount set beyond stacksize limit of 64");
		}
		stack.stackSize = amount;
	}

	@Override
	public int getAmount() {
		return stack.stackSize;
	}

	@Override
	public int getReuseAmount() {
		return reUseAmount;
	}

	@Override
	public void setReuseAmount(int amount) {
		reUseAmount = amount;
	}
	
}
