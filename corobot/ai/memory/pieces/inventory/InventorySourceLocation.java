package corobot.ai.memory.pieces.inventory;

import corobot.ai.memory.helper.HelperBlock;
import corobot.ai.memory.pieces.BlockLocation;
import corobot.ai.memory.pieces.WorldLocation;

public class InventorySourceLocation extends InventorySource {
	
	private WorldLocation location;
	
	public InventorySourceLocation(WorldLocation location) {
		this.location = location;
	}
	
	@Override
	public Object getHash() {
		return HelperBlock.makeHash((int)((BlockLocation) location).getPos().x, (int)((BlockLocation) location).getPos().y, (int)((BlockLocation) location).getPos().z);
	}
	
}
