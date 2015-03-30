package corobot.ai.memory.pieces.inventory;

import corobot.ai.memory.pieces.WorldLocation;

public class InventorySourceLocation extends InventorySource {
	
	private WorldLocation location;
	
	public InventorySourceLocation(WorldLocation location) {
		this.location = location;
	}
	
}
