package corobot.ai.behaviors.misc;

import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.tree.Sequence;

public class MasterPlanSequence extends Sequence {
	
	/**
	 * this is the highest level coordinator
	 * it will activate use of GOAP where needed
	 * this class exists because GOAP cannot determine everything from pure precondition/effect (or so we suspect)
	 * it will do what PlayerAI test code was doing, setting up plans depending on the sequence run
	 * 
	 * survival instincts will vary depending on the point in the sequence
	 * for the most part its to avoid combat unless something gets close enough
	 * 
	 * owner orders will override the running of this probably, unless survival instinct kicks in
	 * 
	 * WIP order (think about how these steps could be converted into GOAP plan chain):
	 * 
	 * - spawn
	 * - set home in flat area
	 * - make wood shovel
	 * - mine dirt
	 * - make dirt house (door placement?!?)
	 * - place workbench at home
	 * 
	 * 
	 * - furnace?
	 * - find more ores?
	 * - break tallgrass for seeds?
	 * 
	 * 
	 */
	
	public MasterPlanSequence(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
		
	}

	@Override
	public EnumBehaviorState tick() {
		return super.tick();
	}
	
}
