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

public class RightClickBlock extends LeafNode {
	
	public Vector3f curBlockPos;

	public State state = State.PATHING;
	
	public int ticksPathing = 0;
	public int ticksPathingMax = 400;
	
	public enum State {
		PATHING, BUILDING, COMPLETE;
	}
	
	public RightClickBlock(BehaviorNode parParent, Blackboard blackboard, Vector3f posCoord) {
		super(parParent, blackboard);
		curBlockPos = posCoord;
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
		Vector3f loc = curBlockPos;//HelperHouse.getBlockToBuild();
		
		if (loc != null) {
			double dist = VecUtil.getDistSqrd(player.getPos(), loc);
			if (dist < 3) {
				ticksPathing = 0;
				state = State.BUILDING;
				
				int x = MathHelper.floor_double(loc.x);
				int y = MathHelper.floor_double(loc.y);
				int z = MathHelper.floor_double(loc.z);
				Block block = worldMC.getBlock(x, y, z);
				
				EntityPlayer playerEnt = Corobot.getPlayerAI().bridgePlayer.getPlayer();
				int bestSlot = UtilPlayer.getSlotToRightClickWith(playerEnt, playerEnt.inventory, true);
				if (bestSlot != -1) {
					playerEnt.inventory.currentItem = bestSlot;
					ItemStack stack = playerEnt.getCurrentEquippedItem();
					
					mc.playerController.onPlayerRightClick(playerEnt, worldMC, stack, x, y, z, 0, Vec3.createVectorHelper(0, 0, 0));
					playerEnt.swingItem();
					
					state = State.COMPLETE;
					System.out.println("clicked " + loc);
					return end(EnumBehaviorState.SUCCESS);
				} else {
					Corobot.dbg("cant find empty slot to use!");
					return end(EnumBehaviorState.FAILURE);
				}
			} else {
				state = State.PATHING;
				if (world.getTicksTotal() % 20 == 0) {
					System.out.println("path to " + loc);
					getBlackboard().setMoveToBest(loc);
				}
				
				ticksPathing++;
				if (ticksPathing >= ticksPathingMax) {
					//Corobot.getPlayerAI().planGoal.invalidatePlan();
					return end(EnumBehaviorState.FAILURE);
				}
			}
			//Corobot.dbg("state: " + state);
		} else {
			Corobot.dbg("cant find location to click!");
			state = State.COMPLETE;
			return end(EnumBehaviorState.FAILURE);
		}
		
		return EnumBehaviorState.RUNNING;
	}
	
	//TODO: replace with proper reset states for behaviors
	public EnumBehaviorState end(EnumBehaviorState state) {
		ticksPathing = 0;
		return state;
	}
	
	/*@Override
	public boolean isTaskComplete() {
		return state == State.COMPLETE;
	}*/

}
