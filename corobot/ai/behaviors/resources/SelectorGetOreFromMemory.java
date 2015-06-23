package corobot.ai.behaviors.resources;

import com.corosus.ai.Blackboard;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.tree.SelectorRoutine;

import corobot.Corobot;
import corobot.ai.BlackboardImpl;
import corobot.ai.memory.helper.HelperBlock;
import corobot.ai.memory.pieces.BlockLocation;
import corobot.util.UtilMemory;

public class SelectorGetOreFromMemory extends SelectorRoutine {

	public SelectorGetOreFromMemory(BehaviorNode parParent,
			Blackboard blackboard) {
		super(parParent, blackboard);
	}
	
	@Override
	public boolean tickTryRoutine() {
		BlackboardImpl bb = (BlackboardImpl) getBlackboard();
		if (HelperBlock.listResources.contains(bb.getBlockToMine())) {
			BlockLocation loc = UtilMemory.getClosestBlockFromMemory(bb.getBlockToMine(), bb.getMetaToMine());
			if (loc != null) {
				bb.setBlockLocationToMine(loc);
				bb.setMoveToBest(loc.getPos());
				return true;
			} else {
				return false;
			}
		} else {
			if (!HelperBlock.listResourcesToNotRemember.contains(bb.getBlockToMine())) {
				Corobot.dbg("resource is not listed as a mineable type!");
			}
		}
		return false;
	}

}
