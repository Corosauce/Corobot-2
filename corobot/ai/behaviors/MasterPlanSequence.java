package corobot.ai.behaviors;

import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.tree.Sequence;

import corobot.ai.behaviors.misc.BuildHouse;
import corobot.ai.minigoap.GoapSequence;

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
	 * 
	 * GOAP for:
	 * - making items
	 * -- pathing
	 * -- gui using
	 * -- inventory management
	 * -- mining
	 * 
	 * adjust GOAP for:
	 * - populating a behavior sequence
	 * - using enum return vals to determine if success, running, failed, interrupted
	 * 
	 * 
	 * 
	 * 
	 */
	
	public MasterPlanSequence(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
		
		this.getChildren().add(new BuildHouse(this, blackboard));
		
		GoapSequence goap = new GoapSequence(parParent, blackboard, "diamond pickaxe");
		goap.createPlan();
		goap.setCreatedPlan();
		
		this.getChildren().add(goap);
		
	}

	@Override
	public EnumBehaviorState tick() {
		return super.tick();
	}
	
}
