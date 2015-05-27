package corobot.ai.behaviors;

import net.minecraft.entity.player.EntityPlayer;

import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.leaf.LeafNode;
import com.corosus.entity.IEntity;

import corobot.ai.PlayerAI;

public class StayAboveWater extends LeafNode {

	
	
	public StayAboveWater(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
	}

	@Override
	public EnumBehaviorState tick() {
		
		PlayerAI playerAI = (PlayerAI) this.getBlackboard().getAgent().getActor();
		EntityPlayer player = playerAI.bridgePlayer.getPlayer();
		
		if (player.isInWater()) {
			player.motionY += 0.08;
		}
		
		return super.tick();
	}

}
