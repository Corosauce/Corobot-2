package corobot.ai.behaviors.misc;

import javax.vecmath.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
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
import corobot.util.UtilPlayer;

public class TaskMineBlock extends BehaviorNode {
	
	//need for sure
	//public BlockLocation loc;
	public int ticksMining = 0;
	public int ticksMiningMax = 120;
	
	public enum State {
		PATHING, MINING, PICKINGUP;
	}
	
	public TaskMineBlock(BehaviorNode parParent, Blackboard blackboard) {
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
		
		//NEW
		
		//get loc from pathto vec
		Vector3f moveTo = bb.getMoveTo();
		
		if (moveTo != null) {
			double dist = VecUtil.getDistSqrd(player.getPos(), moveTo);
			if (dist < 5) {
				int x = MathHelper.floor_double(moveTo.x);
				int y = MathHelper.floor_double(moveTo.y);
				int z = MathHelper.floor_double(moveTo.z);
				Block block = worldMC.getBlock(x, y, z);
				if (block == Blocks.air) {
					HelperBlock.removeEntry(bb.getWorldMemory(), bb.getBlockLocation());
					return EnumBehaviorState.SUCCESS;
				} else {
					//TODO: make this adapt to other tools
					//TODO: something to transfer best tool to hotbar if its not in hotbar
					int bestSlot = UtilPlayer.getBestToolSlot(ItemPickaxe.class, playerEnt, playerEnt.inventory);
					if (bestSlot != -1) {
						playerEnt.inventory.currentItem = bestSlot;
					} else {
						//TODO: fail if we cant mine with bare hands
					}
					Minecraft.getMinecraft().playerController.onPlayerDamageBlock(x, y, z, 2);
					Corobot.getPlayerAI().bridgePlayer.getPlayer().swingItem();
					
					ticksMining++;
					if (ticksMining >= ticksMiningMax) {
						Corobot.dbg("WARNING: mining took too long");
						return EnumBehaviorState.FAILURE;
					}
				}
			} else {
				Corobot.dbg("WARNING: too far from mining position, other task failed to get us in position or we were pushed back");
				return EnumBehaviorState.FAILURE;
			}
		} else {
			Corobot.dbg("CRITICAL: moveTo is null");
			return EnumBehaviorState.FAILURE;
		}
		
		return EnumBehaviorState.RUNNING;
	}
	
	@Override
	public void reset() {
		super.reset();
		this.ticksMining = 0;
	}

}
