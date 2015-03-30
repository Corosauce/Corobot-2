package corobot.ai.memory;

import java.util.ArrayList;
import java.util.List;

import com.corosus.ai.minigoap.IWorldState;

import net.minecraft.item.ItemStack;
import corobot.ai.memory.pieces.InventoryCollection;
import corobot.ai.memory.pieces.MachineLocation;
import corobot.ai.memory.pieces.ResourceLocation;

public class CopyOfPlayerMemoryState implements IWorldState {

	public List<InventoryCollection> listInventories = new ArrayList<InventoryCollection>();
	public List<ResourceLocation> listResourceLocationsFound = new ArrayList<ResourceLocation>();
	public List<MachineLocation> listMachineLocations = new ArrayList<MachineLocation>();
	
	public CopyOfPlayerMemoryState() {
		
	}

	/*@Override
	public boolean hasPreconditionsFor(IWorldState snapshot) {
		return contains((CopyOfPlayerMemoryState)snapshot);
	}*/
	
	public boolean contains(CopyOfPlayerMemoryState sourceState) {
		//go over various types of memory and check if this has what source needs
		
		//TODO: issues, if 2 separate itemstacks add up to a required count, it will still return false
		
		List<ItemStack> listSourceStacks = sourceState.getAllItems();
		List<ItemStack> listOurStacks = getAllItems();
		
		//boolean missingEntries = false;
		
		//compare items
		for (ItemStack stackSource : listSourceStacks) {
			boolean foundEntry = false;
			for (ItemStack stackOurs : listOurStacks) {
				//run vanilla attempt, then if false, run out 'at least stackcount' additional check
				if (ItemStack.areItemStacksEqual(stackOurs, stackSource)) {
					foundEntry = true;
					break;
				} else {
					if (stackSource.getMaxStackSize() > 1 && stackOurs.getMaxStackSize() > 1) {
						if (stackOurs.stackSize >= stackSource.stackSize) {
							foundEntry = true;
							break;
						}
					}
				}
			}
			if (!foundEntry) {
				return false;
			}
		}
		
		//compare resource locations
		for (ResourceLocation locSource : sourceState.listResourceLocationsFound) {
			boolean foundEntry = false;
			for (ResourceLocation loc : listResourceLocationsFound) {
				if (/*loc.getPos().equals(locSource.getPos()) && */loc.getBlock() == locSource.getBlock()) {
					foundEntry = true;
					break;
				}
			}
			if (!foundEntry) {
				return false;
			}
		}
		
		//compare machine locations
		for (MachineLocation locSource : sourceState.listMachineLocations) {
			boolean foundEntry = false;
			for (MachineLocation loc : listMachineLocations) {
				if (/*loc.getPos().equals(locSource.getPos()) && */loc.getBlock() == locSource.getBlock()) {
					foundEntry = true;
					break;
				}
			}
			if (!foundEntry) {
				return false;
			}
		}
		
		return true;
	}
	
	public List<ItemStack> getAllItems() {
		List<ItemStack> allStacks = new ArrayList<ItemStack>();
		for (InventoryCollection col : listInventories) {
			allStacks.addAll(col.getListStacks());
		}
		return allStacks;
	}
	
}
