package corobot.ai.behaviors.resources;

import javax.vecmath.Vector3f;

import com.corosus.ai.Blackboard;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.tree.SelectorRoutine;
import com.corosus.entity.IEntity;

import corobot.Corobot;
import corobot.ai.BlackboardImpl;
import corobot.ai.memory.pieces.BlockLocation;
import corobot.util.UtilMemory;

public class SelectorGetOreFromArea extends SelectorRoutine {

	public SelectorGetOreFromArea(BehaviorNode parParent,
			Blackboard blackboard) {
		super(parParent, blackboard);
	}
	
	@Override
	public boolean tickTryRoutine() {
		IEntity player = Corobot.getPlayerAI();
		BlackboardImpl bb = (BlackboardImpl) getBlackboard();
		Vector3f pos = UtilMemory.getClosestBlockFromArea(bb.getBlockToMine(), bb.getMetaToMine(), player.getPos());
		if (pos != null) {
			BlockLocation loc = new BlockLocation(pos, bb.getBlockToMine());
			bb.setBlockLocationToMine(loc);
			bb.setMoveToBest(loc.getPos());
			return true;
		} else {
			return false;
		}
	}

}
