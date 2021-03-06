package corobot.ai.behaviors.misc;

import net.minecraft.entity.player.EntityPlayer;

import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.leaf.LeafNode;
import com.corosus.entity.IEntity;

import corobot.ai.PlayerAI;

public class JumpForBoredom extends LeafNode {

	public int delay = 0;
	
	public JumpForBoredom(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
	}
	
	public JumpForBoredom(BehaviorNode parParent, Blackboard blackboard, int delay) {
		super(parParent, blackboard);
		this.delay = delay;
	}

	@Override
	public EnumBehaviorState tick() {
		
		PlayerAI playerAI = (PlayerAI) this.getBlackboard().getAgent().getActor();
		EntityPlayer player = playerAI.bridgePlayer.getPlayer();
		if (player.onGround) player.jump();
		
		if (delay > 0) {
			delay--;
			return EnumBehaviorState.RUNNING;
		} else {
			return super.tick();
		}
	}

}
