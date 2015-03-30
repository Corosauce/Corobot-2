package corobot.ai;

import com.corosus.ai.AIBTAgent;
import com.corosus.entity.IEntity;

public class AIBTAgentImpl extends AIBTAgent {

	public AIBTAgentImpl(IEntity parActor) {
		super(parActor);
		setBlackboard(new BlackboardImpl(this));
	}

}
