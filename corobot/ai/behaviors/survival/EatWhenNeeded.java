package corobot.ai.behaviors.survival;

import net.minecraft.entity.player.EntityPlayer;

import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.leaf.LeafNodeBB;
import com.corosus.entity.IEntity;

import corobot.ai.PlayerAI;
import corobot.ai.memory.helper.HelperInventory;
import corobot.ai.memory.helper.HelperItemUsing;
import corobot.ai.memory.helper.HelperItemUsing.ItemUse;
import corobot.util.UtilPlayer;

public class EatWhenNeeded extends LeafNodeBB {

	
	
	public EatWhenNeeded(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
	}

	@Override
	public EnumBehaviorState tick() {
		
		PlayerAI playerAI = (PlayerAI) this.getBlackboard().getAgent().getActor();
		EntityPlayer player = playerAI.bridgePlayer.getPlayer();
		
		if (player.getFoodStats().getFoodLevel() < 14 || (player.getHealth() < 18 && player.getFoodStats().getFoodLevel() <= 17)) {
			int slot = UtilPlayer.getBestFoodSlot(player, player.inventory, true);
			if (slot != -1) {
				if (!HelperItemUsing.isUsingItem()) {
					HelperItemUsing.setInUse(ItemUse.FOOD, slot);
				}
			}
		}
		
		return super.tick();
	}

}
