package corobot.ai.minigoap.plans;

import java.util.List;

import javax.vecmath.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.corosus.ai.AIBTAgent;
import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.nodes.tree.Sequence;
import com.corosus.ai.minigoap.IWorldStateProperty;
import com.corosus.ai.minigoap.PlanPiece;
import com.corosus.entity.IEntity;
import com.corosus.util.VecUtil;
import com.corosus.world.IWorld;

import corobot.Corobot;
import corobot.ai.BlackboardImpl;
import corobot.ai.behaviors.misc.TaskFindNearbyItem;
import corobot.ai.behaviors.misc.TaskMineBlock;
import corobot.ai.behaviors.misc.TaskMoveToPos;
import corobot.ai.memory.helper.HelperBlock;
import corobot.ai.memory.helper.HelperInventory;
import corobot.ai.memory.pieces.BlockLocation;
import corobot.ai.memory.pieces.ItemEntry;
import corobot.ai.memory.pieces.ResourceLocation;
import corobot.ai.memory.pieces.inventory.InventorySourceSelf;
import corobot.util.UtilEnt;
import corobot.util.UtilInventory;
import corobot.util.UtilMemory;
import corobot.util.UtilPlayer;

public class PlanMineBlockNewSequence extends PlanPiece {

	//should be close to location be a precondition?
	
	//should we add a move to location plan? might be a good idea
	//should we add a pickup item plan?
	
	//how does item counts factor into this?
	//- for now just do 1 and see where it goes, but some recipes will require more than 1.....
	//- needs a sort of dynamic plan that can pass requirements along to other plans, fuzzy conditions ?
	
	public Block block;
	public ItemStack droppedItem = null;
	public int meta;
	public ItemStack neededTool = null;
	public State state = State.PATHING;
	public int countNeeded = 1;
	
	public Sequence sequenceTasks;
	
	public enum State {
		PATHING, MINING, PICKINGUP;
	}
	
	public PlanMineBlockNewSequence(String planName, Blackboard blackboard, Block block, int meta, ItemStack tool) {
		super(planName, blackboard);
		this.block = block;
		this.meta = meta;
		this.neededTool = tool;
		this.droppedItem = new ItemStack(block);
		
		if (neededTool != null) {
			this.getPreconditions().getProperties().add(new ItemEntry(neededTool, new InventorySourceSelf()));
		}
		
		this.getEffects().getProperties().add(new ItemEntry(new ItemStack(block), new InventorySourceSelf()));
		this.getPreconditions().getProperties().add(new ResourceLocation(null, block, meta));
		
		
	}
	
	public PlanMineBlockNewSequence(String planName, Blackboard blackboard, ItemStack itemReturned, Block block, int meta, ItemStack tool) {
		super(planName, blackboard);
		this.block = block;
		this.meta = meta;
		this.neededTool = tool;
		this.droppedItem = itemReturned;
		
		if (neededTool != null) {
			this.getPreconditions().getProperties().add(new ItemEntry(neededTool, new InventorySourceSelf()));
		}
		
		this.getEffects().getProperties().add(new ItemEntry(itemReturned, new InventorySourceSelf()));
		this.getPreconditions().getProperties().add(new ResourceLocation(null, block, meta));
	}
	
	public PlanMineBlockNewSequence(PlanPiece obj) {
		super(obj, obj.getBlackboard());
		block = ((PlanMineBlockNewSequence)obj).block;
		meta = ((PlanMineBlockNewSequence)obj).meta;
		neededTool = ((PlanMineBlockNewSequence)obj).neededTool;
		countNeeded = ((PlanMineBlockNewSequence)obj).countNeeded;
		droppedItem = ((PlanMineBlockNewSequence)obj).droppedItem;
		
		/*TOP TASK:
		 * - find block to mine
		 * - feed location into moveTo
		 * - how do we handle mining???
		 * - how do we use second moveTo????
		 */
		
		/*SUB TASKS:
		 * - moveto mineable block
		 * - mine it
		 * - move to dropped pickup
		 */
		
		
		sequenceTasks = new Sequence(obj, getBlackboard());
		
		TaskMoveToPos moveTo = new TaskMoveToPos(sequenceTasks, getBlackboard());
		
		sequenceTasks.add(moveTo);
		sequenceTasks.add(new TaskMineBlock(sequenceTasks, getBlackboard()));
		sequenceTasks.add(new TaskFindNearbyItem(sequenceTasks, getBlackboard()));
		
		//sometimes this one is never fully used if we are already super close to item to pickup, which is actually quite often
		sequenceTasks.add(moveTo);
	}
	
	@Override
	public EnumBehaviorState tick() {
		
		//System.out.println("mine block plan");
		
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		BlackboardImpl bb = (BlackboardImpl) agent.getBlackboard();
		World worldMC = Minecraft.getMinecraft().theWorld;
		
		//execute taskSequence
		//handle communication between subtasks?
		//eg: tell moveto where to go to for where item to pickup is
		//how does that moveto know when its done? just assume when its close enough?
		//use the damn blackboard
		
		System.out.println("cur seq index for mining: " + sequenceTasks.getActiveBehaviorIndex());
		
		BlockLocation loc = null;
		
		//init stuff for sequence
		if (sequenceTasks.getActiveBehaviorIndex() == -1) {
			loc = UtilMemory.getClosestBlock(block, meta);
			if (loc != null) {
				bb.setBlockLocation(loc);
				bb.setMoveToBest(loc.getPos());
			}
			
			bb.setItemToPickup(droppedItem);
		}
		
		EnumBehaviorState result = sequenceTasks.tick();
		
		
		if (isTaskComplete()) {
			return EnumBehaviorState.SUCCESS;
		} else {
			return EnumBehaviorState.RUNNING;
		}
	}
	
	public boolean isTaskComplete() {
		return UtilInventory.getItemCount(Corobot.playerAI.bridgePlayer.getPlayer().inventory, droppedItem/*Item.getItemFromBlock(this.block)*/) >= this.countNeeded;
	}
	
	@Override
	public void reset() {
		super.reset();
	}

}
