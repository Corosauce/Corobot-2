package corobot.ai.behaviors.misc;

import net.minecraft.entity.player.EntityPlayer;

import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.leaf.LeafNode;

import corobot.ai.PlayerAI;

public class HomeBuildAndRepair extends LeafNode {

	private int respawnTimer;

	public HomeBuildAndRepair(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
	}

	@Override
	public EnumBehaviorState tick() {
		
		PlayerAI playerAI = (PlayerAI) this.getBlackboard().getAgent().getActor();
		EntityPlayer player = playerAI.bridgePlayer.getPlayer();
		
		
		
		return super.tick();
	}

}
