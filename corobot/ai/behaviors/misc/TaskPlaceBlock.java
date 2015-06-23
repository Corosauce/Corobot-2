package corobot.ai.behaviors.misc;

import javax.vecmath.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.corosus.ai.AIBTAgent;
import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.leaf.LeafNode;
import com.corosus.entity.IEntity;
import com.corosus.util.VecUtil;
import com.corosus.world.IWorld;

import corobot.Corobot;
import corobot.ai.BlackboardImpl;
import corobot.ai.memory.helper.HelperHouse;
import corobot.ai.memory.pieces.BlockLocation;
import corobot.util.UtilPlayer;

public class TaskPlaceBlock extends LeafNode {
	
	public enum State {
		PATHING, BUILDING, COMPLETE;
	}
	
	public TaskPlaceBlock(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
	}
	
	/*public BuildHouse(PlanPiece obj) {
		super(obj);
		//curBlockPos = ((BuildHouse)obj).curBlockPos;
	}*/
	
	@Override
	public EnumBehaviorState tick() {
		
		//HelperHouse.init();
		
		//System.out.println("mine block plan");
		
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		BlackboardImpl bb = (BlackboardImpl) agent.getBlackboard();
		World worldMC = Minecraft.getMinecraft().theWorld;
		Minecraft mc = Minecraft.getMinecraft();
		
		//BlockLocation loc = UtilMemory.getClosestBlock(block, meta);
		BlockLocation loc = bb.getBlockLocationToPlace();
		
		if (loc != null) {
			double dist = VecUtil.getDistSqrd(player.getPos(), loc.getPos());
			if (dist < 5) {
				
				int x = MathHelper.floor_double(loc.getPos().x);
				int y = MathHelper.floor_double(loc.getPos().y);
				int z = MathHelper.floor_double(loc.getPos().z);
				Block block = worldMC.getBlock(x, y, z);
				
				EntityPlayer playerEnt = Corobot.getPlayerAI().bridgePlayer.getPlayer();
				int bestSlot = UtilPlayer.getSlotForItem(new ItemStack(HelperHouse.getBlockHouseMaterial()), playerEnt, playerEnt.inventory, true);
				if (bestSlot != -1) {
					playerEnt.inventory.currentItem = bestSlot;
					ItemStack stack = playerEnt.getCurrentEquippedItem();
					
					if (block == Blocks.tallgrass || block == Blocks.double_plant) {
						mc.playerController.onPlayerDamageBlock(x, y, z, 2);
					}
					
					//stack.tryPlaceItemIntoWorld(playerEnt, worldMC, x, y, z, 0, 0, 0, 0);
					System.out.println("block at coords we want to place something on: " + worldMC.getBlock(x, y, z));
					mc.playerController.onPlayerRightClick(playerEnt, worldMC, stack, x, y, z, 0, Vec3.createVectorHelper(0, 0, 0));
					playerEnt.swingItem();
					
					return EnumBehaviorState.SUCCESS;
				} else {
					Corobot.dbg("nothing to place block with!");
				}
			} else {
				Corobot.dbg("WARNING: too far from mining position, other task failed to get us in position or we were pushed back");
				return EnumBehaviorState.FAILURE;
			}
			//Corobot.dbg("state: " + state);
		} else {
			Corobot.dbg("CRITICAL: getBlockLocationToPlace is null");
			return EnumBehaviorState.FAILURE;
		}
		
		return EnumBehaviorState.RUNNING;
	}
	
	@Override
	public void reset() {
		super.reset();
	}

}
