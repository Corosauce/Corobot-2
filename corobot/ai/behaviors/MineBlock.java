package corobot.ai.behaviors;

import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.leaf.LeafNodeBB;

public class MineBlock extends LeafNodeBB {

	public MineBlock(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
	}
	
	@Override
	public EnumBehaviorState tick() {
		return super.tick();
	}
	

}
