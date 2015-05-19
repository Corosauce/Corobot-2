package corobot.ai.memory.pieces;

import java.util.ArrayList;
import java.util.List;

import com.corosus.ai.minigoap.IWorldStateProperty;

import corobot.ai.memory.pieces.inventory.InventorySource;
import net.minecraft.item.ItemStack;

@Deprecated
public class InventoryCollection implements IWorldStateProperty {

	//possibly not quite usable.... needs to be broken down more for goap PlanPiece
	//maybe use it for memory but not goap if possible?
	
	private List<ItemStack> listStacks = new ArrayList<ItemStack>();
	private InventorySource source;
	
	public InventoryCollection(List<ItemStack> listStacks, InventorySource source) {
		this.listStacks = listStacks;
		this.source = source;
	}
	
	public List<ItemStack> getListStacks() {
		return listStacks;
	}
	
	public void setListStacks(List<ItemStack> listStacks) {
		this.listStacks = listStacks;
	}
	
	public InventorySource getSource() {
		return source;
	}
	
	public void setSource(InventorySource source) {
		this.source = source;
	}

	@Override
	public boolean canEffectSatisfyPrecondition(IWorldStateProperty precondition) {
		return false;
	}
	
	
	
}
