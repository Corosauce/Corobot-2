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
import corobot.ai.behaviors.misc.TaskConstructPath;
import corobot.ai.behaviors.misc.TaskFindNearbyItem;
import corobot.ai.behaviors.misc.TaskMoveToPos;
import corobot.ai.behaviors.resources.SelectorGetOreFromArea;
import corobot.ai.behaviors.resources.SelectorGetOreFromMemory;
import corobot.ai.behaviors.resources.TaskMineBlock;
import corobot.ai.behaviors.resources.TaskSearchForResource;
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

public class PlanGetResource extends PlanPiece {

	//should be close to location be a precondition?
	
	//should we add a move to location plan? might be a good idea
	//should we add a pickup item plan?
	
	//how does item counts factor into this?
	//- for now just do 1 and see where it goes, but some recipes will require more than 1.....
	//- needs a sort of dynamic plan that can pass requirements along to other plans, fuzzy conditions ?
	
	//going to set the max stack size for this, since this plan can provide an unlimited amount of resources, just needs more time... not factoring in tool breaking etc
	
	//TODO: i messed up the implementation of searching for resources if they cant be found, clean this up
	//i think best way would be a decorator that decides between to go memory block vs find one
	//TODO: from class it was moved from: new search sequence needs to backtrack out of this task back to fresh start of PlanMineBlock once it actually finds something
	
	public Block block;
	public ItemStack droppedItem = null;
	public int meta;
	public ItemStack neededTool = null;
	public State state = State.PATHING;
	//public int countNeeded = 1;
	
	public int amountItCanProvide = 64;
	
	public Sequence sequenceTasksNew;
	
	public Sequence sequenceTasks;

	
	//only used if we dont have resources in memory or nearby
	//public Sequence sequenceFindResources;
	
	public enum State {
		PATHING, MINING, PICKINGUP;
	}
	
	public PlanGetResource(String planName, Blackboard blackboard, Block block, int meta, ItemStack tool) {
		super(planName, blackboard);

		this.droppedItem = new ItemStack(block, amountItCanProvide);
		
		setStates(planName, blackboard, block, meta, tool, droppedItem);
		
		//remove knowing where resources are as a requirement
		/*if (!HelperBlock.listResourcesToNotRemember.contains(block)) {
			this.getPreconditions().getProperties().add(new ResourceLocation(null, block, meta));
		}*/
		
		
	}
	
	public PlanGetResource(String planName, Blackboard blackboard, ItemStack itemReturned, Block block, int meta, ItemStack tool) {
		super(planName, blackboard);
		
		this.droppedItem = itemReturned;
		
		//only adjust the item for world properties, not droppedItem
		itemReturned.stackSize = amountItCanProvide;
		
		setStates(planName, blackboard, block, meta, tool, itemReturned);
	}
	
	public void setStates(String planName, Blackboard blackboard, Block block, int meta, ItemStack tool, ItemStack droppedItem) {
		this.block = block;
		this.meta = meta;
		this.neededTool = tool;
		
		if (neededTool != null) {
			this.getPreconditions().getProperties().add(new ItemEntry(neededTool, new InventorySourceSelf()));
		}
		
		//this.getEffects().getProperties().add(new ItemEntry(new ItemStack(block, amountItCanProvide), new InventorySourceSelf()));
		this.getEffects().getProperties().add(new ItemEntry(droppedItem, new InventorySourceSelf()));
	}
	
	public PlanGetResource(PlanPiece obj) {
		super(obj, obj.getBlackboard());
		block = ((PlanGetResource)obj).block;
		meta = ((PlanGetResource)obj).meta;
		neededTool = ((PlanGetResource)obj).neededTool;
		//countNeeded = ((PlanGetResource)obj).countNeeded;
		setAquireAmount(((PlanGetResource)obj).getAquireAmount());
		droppedItem = ((PlanGetResource)obj).droppedItem;
		
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
		

		
		//sequenceFindResources = new Sequence(this, getBlackboard());
		//sequenceFindResources.add(new TaskSearchForResource(this, getBlackboard()));
		//unneeded since it moves on for now
		//sequenceFindResources.add(new TaskMoveToPos(this, getBlackboard()));
		
		SelectorGetOreFromMemory seqMemory = new SelectorGetOreFromMemory(sequenceTasksNew, getBlackboard());
		SelectorGetOreFromArea seqArea = new SelectorGetOreFromArea(seqMemory, getBlackboard());
		seqMemory.add(seqArea);
		
		//TODO: add to seqArea
		//some sort of generic profile based block mining task
		//this is temp, a profiling class needs to invoke this taskconstructpath class
		seqArea.add(new TaskConstructPath(seqArea, getBlackboard()));
		
		sequenceTasksNew = new Sequence(obj, getBlackboard());
		sequenceTasksNew.add(seqMemory);
		moveTo = new TaskMoveToPos(sequenceTasks, getBlackboard());
		
		sequenceTasksNew.add(moveTo);
		sequenceTasksNew.add(new TaskMineBlock(sequenceTasks, getBlackboard()));
		sequenceTasksNew.add(new TaskFindNearbyItem(sequenceTasks, getBlackboard()));
		
		//sometimes this one is never fully used if we are already super close to item to pickup, which is actually quite often
		sequenceTasksNew.add(moveTo);
	}
	
	@Override
	public void initTask(PlanPiece piece,
			IWorldStateProperty effectRequirement,
			IWorldStateProperty preconditionRequirement) {
		super.initTask(piece, effectRequirement, preconditionRequirement);
		
		if (piece.isRealInstance()) {
			setAquireAmount(piece.getAquireAmount());
			System.out.println("SETTING COUNT FROM REAL INSTANCE: " + getAquireAmount());
		} else {
			//TODO: verify this works - recipe crafting needs/has this too
			if (preconditionRequirement instanceof ItemEntry) {
				ItemEntry entry = (ItemEntry) preconditionRequirement;
				
				//TODO: i feel like this should increment from the effect, not precondition, to be more accurate, but currently we set effect to be 64 stack size.... hmmmm
				setAquireAmount(entry.getStack().stackSize);
				System.out.println("SETTING COUNT: " + getAquireAmount());
			}
		}
		
	}
	
	@Override
	public void addToTask(PlanPiece piece,
			IWorldStateProperty effectRequirement,
			IWorldStateProperty preconditionRequirement) {
		super.addToTask(piece, effectRequirement, preconditionRequirement);
		
		if (preconditionRequirement instanceof ItemEntry) {
			ItemEntry entry = (ItemEntry) preconditionRequirement;
			
			setAquireAmount(getAquireAmount() + entry.getStack().stackSize);
			System.out.println("INCREMENTING COUNT: " + getAquireAmount());
		}
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
		
		//System.out.println("cur seq index for mining: " + sequenceTasks.getActiveBehaviorIndex());
		
		BlockLocation loc = null;
		
		//init stuff for sequence
		if (sequenceTasksNew.getActiveBehaviorIndex() == -1) {
			bb.setBlockToMine(block);
			bb.setMetaToMine(meta);
			bb.setItemToPickup(droppedItem);
			
			
			//relocate to selectors
			/*if (HelperBlock.listResources.contains(block)) {
				loc = UtilMemory.getClosestBlockFromMemory(block, meta);
			} else {
				Vector3f pos = UtilMemory.getClosestBlockFromArea(block, meta, player.getPos());
				if (pos != null) {
					loc = new BlockLocation(pos, block);
				}
			}

			if (loc != null) {
				bb.setBlockLocation(loc);
				bb.setMoveToBest(loc.getPos());
			} else {
				Corobot.dbg("CRITICAL: cant find resource to mine!!: " + block);
				return trySearch();
			}*/
			
			
		}
		
		EnumBehaviorState result = sequenceTasksNew.tick();
		
		
		if (isTaskComplete()) {
			return EnumBehaviorState.SUCCESS;
		} else {
			return EnumBehaviorState.RUNNING;
		}
	}
	
	/*public EnumBehaviorState trySearch() {
		Corobot.dbg("INFO: trying resource search!");
		EnumBehaviorState result = sequenceFindResources.tick();
		return result;
	}*/
	
	public boolean isTaskComplete() {
		return UtilInventory.getItemCount(Corobot.playerAI.bridgePlayer.getPlayer().inventory, droppedItem/*Item.getItemFromBlock(this.block)*/) >= getAquireAmount();
	}
	
	@Override
	public void reset() {
		super.reset();
	}

}
