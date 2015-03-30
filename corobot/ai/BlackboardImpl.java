package corobot.ai;

import com.corosus.ai.AIBTAgent;
import com.corosus.ai.Blackboard;
import com.corosus.ai.minigoap.IWorldState;

import corobot.ai.memory.PlayerMemory;

public class BlackboardImpl extends Blackboard {
	
	public BlackboardImpl(AIBTAgent parAgent) {
		super(parAgent);
		this.setWorldMemory(new PlayerMemory(this));
	}

	public PlayerMemory getPlayerMemory() {
		return (PlayerMemory) getWorldMemory();
	}
	
	

}
