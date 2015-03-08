package corobot.ai.behaviors;

import net.minecraft.entity.Entity;

import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.leaf.LeafNodeBB;
import com.corosus.entity.IEntity;
import com.corosus.util.VecUtil;

import corobot.Corobot;
import corobot.bridge.TargetBridge;

public class TrackAndAttackEntity extends LeafNodeBB {
	
	public TrackAndAttackEntity(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
	}

	@Override
	public EnumBehaviorState tick() {
		
		float attackRange = 4F;
		
		IEntity player = this.getBlackboard().getAgent().getActor();
		IEntity target = this.getBlackboard().getTargetAttack();
		if (target != null) {
			if (target instanceof TargetBridge) {
				//Entity targetEnt = ((TargetBridge)target).target;
				
				//if (player.getLevel().getTicksTotal() % 10 == 0) {
					if (VecUtil.getDistSqrd(player.getPos(), target.getPos()) > attackRange) {
						player.setMoveTo(target.getPos());
					}
				//}
				
				if (VecUtil.getDistSqrd(player.getPos(), target.getPos()) <= attackRange) {
					Corobot.playerAI.bridgePlayer.attackTargetMelee(target);
				}
			}
		}
		
		return super.tick();
	}

}
