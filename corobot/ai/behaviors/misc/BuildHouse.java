package corobot.ai.behaviors.misc;

import javax.vecmath.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
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
import corobot.ai.memory.helper.HelperHouse;
import corobot.util.UtilPlayer;

public class BuildHouse extends LeafNode {
	
	public Vector3f curBlockPos;

	public State state = State.PATHING;
	
	public int ticksPathing = 0;
	public int ticksPathingMax = 200;
	
	public Vector3f curPillarPos;
	public int curPillarHeight = 0;
	
	public enum State {
		PATHING, BUILDING, COMPLETE;
	}
	
	public BuildHouse(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
		HelperHouse.init();
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
		Blackboard bb = agent.getBlackboard();
		World worldMC = Minecraft.getMinecraft().theWorld;
		Minecraft mc = Minecraft.getMinecraft();
		
		//BlockLocation loc = UtilMemory.getClosestBlock(block, meta);
		Vector3f loc = HelperHouse.getBlockToBuild();
		
		if (loc != null) {
			double dist = VecUtil.getDistSqrd(player.getPos(), loc);
			if (dist < 5) {
				
				state = State.BUILDING;
				
				int x = MathHelper.floor_double(loc.x);
				int y = MathHelper.floor_double(loc.y);
				int z = MathHelper.floor_double(loc.z);
				Block block = worldMC.getBlock(x, y, z);
				
				EntityPlayer playerEnt = Corobot.getPlayerAI().bridgePlayer.getPlayer();
				int bestSlot = UtilPlayer.getSlotForItem(new ItemStack(Blocks.dirt), playerEnt, playerEnt.inventory, true);
				if (bestSlot != -1) {
					playerEnt.inventory.currentItem = bestSlot;
					ItemStack stack = playerEnt.getCurrentEquippedItem();
					
					if (block == Blocks.tallgrass || block == Blocks.double_plant) {
						mc.playerController.onPlayerDamageBlock(x, y, z, 2);
					}
					
					//stack.tryPlaceItemIntoWorld(playerEnt, worldMC, x, y, z, 0, 0, 0, 0);
					mc.playerController.onPlayerRightClick(playerEnt, worldMC, stack, x, y, z, 0, Vec3.createVectorHelper(0, 0, 0));
					playerEnt.swingItem();
				}
			} else {
				state = State.PATHING;
				if (world.getTicksTotal() % 20 == 0) {
					getBlackboard().setMoveToBest(loc);
				}
				
				ticksPathing++;
				if (ticksPathing >= ticksPathingMax) {
					return EnumBehaviorState.FAILURE;
					//Corobot.getPlayerAI().planGoal.invalidatePlan();
				}
			}
			//Corobot.dbg("state: " + state);
		} else {
			Corobot.dbg("house complete!");
			state = State.COMPLETE;
			return EnumBehaviorState.SUCCESS;
		}
		
		if (isTaskComplete()) {
			return EnumBehaviorState.SUCCESS;
		} else {
			return EnumBehaviorState.RUNNING;
		}
	}
	
	public boolean isTaskComplete() {
		return state == State.COMPLETE;
	}

}
