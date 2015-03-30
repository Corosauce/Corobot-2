package corobot.ai.memory;

import java.util.ArrayList;
import java.util.List;

import corobot.ai.BlackboardImpl;
import corobot.ai.memory.pieces.InventoryCollection;
import corobot.ai.memory.pieces.MachineLocation;
import corobot.ai.memory.pieces.ResourceLocation;

public class PlayerMemory extends PlayerMemoryState {

	private BlackboardImpl blackboard;
	
	public PlayerMemory(BlackboardImpl blackboard) {
		this.blackboard = blackboard;
	}
	
}
