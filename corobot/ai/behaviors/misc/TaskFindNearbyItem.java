package corobot.ai.behaviors.misc;

import javax.vecmath.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.corosus.ai.AIBTAgent;
import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.entity.IEntity;
import com.corosus.util.VecUtil;
import com.corosus.world.IWorld;

import corobot.Corobot;
import corobot.ai.BlackboardImpl;
import corobot.ai.memory.helper.HelperBlock;
import corobot.util.UtilEnt;
import corobot.util.UtilPlayer;

public class TaskFindNearbyItem extends BehaviorNode {
	
	//need for sure
	//public BlockLocation loc;
	public int ticksLooking = 0;
	public int ticksLookingMax = 20;
	
	public enum State {
		PATHING, MINING, PICKINGUP;
	}
	
	public TaskFindNearbyItem(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
	}
	
	@Override
	public EnumBehaviorState tick() {
		
		//System.out.println("mine block plan");
		
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		BlackboardImpl bb = (BlackboardImpl) agent.getBlackboard();
		World worldMC = Minecraft.getMinecraft().theWorld;
		EntityPlayer playerEnt = Corobot.getPlayerAI().bridgePlayer.getPlayer();
		
		//TODO: nothing ever actually sets this null, we should set this null when we know we have what we wanted?
		if (bb.getItemToPickup() == null) {
			Corobot.dbg("CRITICAL: no item set to find!");
			return EnumBehaviorState.FAILURE;
		}
		
		EntityItem closestItem = UtilEnt.getClosestItem(worldMC, player.getPos(), bb.getItemToPickup().getItem());
		if (closestItem != null) {
			getBlackboard().setMoveToBest(new Vector3f((float)closestItem.posX, (float)closestItem.posY, (float)closestItem.posZ));
			return EnumBehaviorState.SUCCESS;
		} else {
			ticksLooking++;
			if (ticksLooking > ticksLookingMax) {
				Corobot.dbg("CRITICAL: couldnt find item!");
				return EnumBehaviorState.FAILURE;
			}
		}
		
		return EnumBehaviorState.RUNNING;
	}
	
	@Override
	public void reset() {
		super.reset();
		this.ticksLooking = 0;
	}

}
