package corobot.ai.memory.pieces;

import java.util.ArrayList;
import java.util.List;

import com.corosus.ai.minigoap.IWorldStateProperty;

import corobot.ai.memory.pieces.inventory.InventorySource;
import net.minecraft.item.ItemStack;

public class ItemEntry implements IWorldStateProperty {

	private ItemStack stack;
	private InventorySource source;
	
	public ItemEntry(ItemStack stack, InventorySource source) {
		this.stack = stack;
		this.source = source;
	}
	
	public InventorySource getSource() {
		return source;
	}
	
	public void setSource(InventorySource source) {
		this.source = source;
	}

	@Override
	public boolean canEffectSatisfyPrecondition(IWorldStateProperty precondition) {
		if (precondition instanceof ItemEntry) {
			ItemEntry precond = (ItemEntry) precondition;
			if (ItemStack.areItemStacksEqual(stack, precond.stack)) {
				return true;
			} else {
				if (stack.isItemEqual(precond.stack)) {
					if (stack.getMaxStackSize() > 1 && precond.stack.getMaxStackSize() > 1) {
						if (stack.stackSize >= precond.stack.stackSize) {
							return true;
						}
					}
				}
			}
			return false;
		} else {
			return false;
		}
	}
	
	
	
}
