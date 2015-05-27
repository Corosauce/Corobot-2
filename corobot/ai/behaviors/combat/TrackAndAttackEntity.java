package corobot.ai.behaviors.combat;

import net.minecraft.entity.Entity;

import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.leaf.LeafNode;
import com.corosus.entity.IEntity;
import com.corosus.util.VecUtil;

import corobot.Corobot;
import corobot.ai.memory.helper.HelperItemUsing;
import corobot.ai.memory.helper.HelperPath;
import corobot.ai.memory.helper.HelperPath.Repaths;
import corobot.bridge.TargetBridge;
import corobot.util.UtilEnt;

public class TrackAndAttackEntity extends LeafNode {
	
	public TrackAndAttackEntity(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
	}

	@Override
	public EnumBehaviorState tick() {
		
		float attackRange = 5F;
		float stopPathRange = 4F;
		
		boolean alwaysLook = true;
		int lookSpeed = 5;
		
		IEntity player = this.getBlackboard().getAgent().getActor();
		IEntity target = this.getBlackboard().getTargetAttack();
		if (target != null) {
			
			/*System.out.println("try attack - this shouldnt be running");
			if (true) return EnumBehaviorState.SUCCESS;*/
			
			//unused instance check
			if (target instanceof TargetBridge) {
				Entity targetEnt = ((TargetBridge)target).target;
				
				//if (player.getLevel().getTicksTotal() % 20 == 0) {
				if (HelperPath.pathNow(Repaths.MAIN)) {
					if (VecUtil.getDistSqrd(player.getPos(), target.getPos()) > attackRange) {
						if (targetEnt.onGround || targetEnt.isInWater()) {
							HelperPath.pathed(Repaths.MAIN);
							player.setMoveTo(target.getPos());
						}
					}
				}
				
				if (VecUtil.getDistSqrd(player.getPos(), target.getPos()) <= attackRange) {
					if (player.getLevel().getTicksTotal() % 5 == 0) {
						if (!HelperItemUsing.isUsingItem()) {
							Corobot.playerAI.bridgePlayer.attackTargetMelee(target);
						}
					}
					UtilEnt.faceEntity(Corobot.playerAI.bridgePlayer.getPlayer(), ((TargetBridge) target).target, lookSpeed, lookSpeed);
				}
				
				//keep distance
				if (VecUtil.getDistSqrd(player.getPos(), target.getPos()) <= stopPathRange) {
					getBlackboard().getPath().clearPath();
				}
				
				if (alwaysLook) {
					UtilEnt.faceEntity(Corobot.playerAI.bridgePlayer.getPlayer(), ((TargetBridge) target).target, lookSpeed, 90);
					Corobot.playerAI.bridgePlayer.getPlayer().rotationPitch += 30;
				}
			}
		}
		
		return super.tick();
	}

}
