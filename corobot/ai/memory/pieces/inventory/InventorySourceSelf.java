package corobot.ai.memory.pieces.inventory;

import corobot.ai.memory.helper.HelperInventory;


public class InventorySourceSelf extends InventorySource {

	public InventorySourceSelf() {
		
	}
	
	@Override
	public Object getHash() {
		return HelperInventory.selfInventoryHash;
	}
	
}
