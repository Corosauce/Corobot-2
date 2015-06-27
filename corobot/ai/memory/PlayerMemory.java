package corobot.ai.memory;

import corobot.ai.BlackboardImpl;

public class PlayerMemory extends PlayerMemoryState {

	private BlackboardImpl blackboard;
	
	public PlayerMemory(BlackboardImpl blackboard) {
		this.blackboard = blackboard;
	}
	
}
