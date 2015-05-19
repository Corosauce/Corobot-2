package corobot.ai.memory.pieces.inventory;

import java.util.UUID;

public class InventorySourceOtherEntity extends InventorySource {

	private UUID entityID;
	
	public InventorySourceOtherEntity(UUID entityID) {
		this.entityID = entityID;
	}
	
	@Override
	public Object getHash() {
		return entityID.toString();
	}
	
}
