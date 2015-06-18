package corobot.ai.behaviors.yd;

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
import com.corosus.ai.minigoap.IWorldStateProperty;
import com.corosus.ai.minigoap.PlanPiece;
import com.corosus.entity.IEntity;
import com.corosus.util.VecUtil;
import com.corosus.world.IWorld;

import corobot.Corobot;
import corobot.ai.memory.helper.HelperBlock;
import corobot.ai.memory.helper.HelperInventory;
import corobot.ai.memory.helper.HelperItemUsing;
import corobot.ai.memory.helper.HelperItemUsing.ItemUse;
import corobot.ai.memory.pieces.BlockLocation;
import corobot.ai.memory.pieces.ItemEntry;
import corobot.ai.memory.pieces.ResourceLocation;
import corobot.ai.memory.pieces.inventory.InventorySourceSelf;
import corobot.bridge.TargetBridge;
import corobot.util.UtilEnt;
import corobot.util.UtilInventory;
import corobot.util.UtilMemory;
import corobot.util.UtilPlayer;

public class PlanSearchMineBlock extends PlanPiece {

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
	public int ticksPickingUp = 0;
	public int ticksPickingUpMax = 80;
	public int ticksMining = 0;
	public int ticksMiningMax = 120;
	public int ticksPathing = 0;
	public int ticksPathingMax = 300;
	
	public enum State {
		PATHING, MINING, PICKINGUP;
	}
	
	public PlanSearchMineBlock(String planName, Blackboard blackboard, Block block, int meta, ItemStack tool) {
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
	
	public PlanSearchMineBlock(String planName, Blackboard blackboard, ItemStack itemReturned, Block block, int meta, ItemStack tool) {
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
	
	public PlanSearchMineBlock(PlanPiece obj) {
		super(obj, obj.getBlackboard());
		block = ((PlanSearchMineBlock)obj).block;
		meta = ((PlanSearchMineBlock)obj).meta;
		neededTool = ((PlanSearchMineBlock)obj).neededTool;
		countNeeded = ((PlanSearchMineBlock)obj).countNeeded;
		droppedItem = ((PlanSearchMineBlock)obj).droppedItem;
	}
	
	@Override
	public EnumBehaviorState tick() {
		
		//System.out.println("mine block plan");
		
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		Blackboard bb = agent.getBlackboard();
		World worldMC = Minecraft.getMinecraft().theWorld;
		
		boolean alwaysLook = true;
		int lookSpeed = 5;
		
		/*Blackboard bb = agent.getBlackboard();
		
		List<IWorldStateProperty> props = bb.getWorldMemory().getProperties();
		
		ResourceLocation loc = null;
		
		for (IWorldStateProperty prop : props) {
			if (prop instanceof ResourceLocation) {
				
				if (((ResourceLocation)prop).getBlock() == block) {
					loc = (ResourceLocation)prop;
					break;
				}
			}
		}*/
		
		if (state == State.PICKINGUP) {
			
			EntityItem closestItem = UtilEnt.getClosestItem(worldMC, player.getPos(), this.droppedItem.getItem());
			if (closestItem != null) {
				if (world.getTicksTotal() % 40 == 0) {
					getBlackboard().setMoveToBest(new Vector3f((float)closestItem.posX, (float)closestItem.posY, (float)closestItem.posZ));
				}
				
			} else {
				ticksPickingUp = 0;
				HelperInventory.updateCache(bb.getWorldMemory(), HelperInventory.selfInventory, Corobot.playerAI.bridgePlayer.getPlayer().inventory);
				//it either cant find anything or it picked it up, so just assume its going to be complete or find next needed block
				state = State.PATHING;
				//Corobot.getPlayerAI().planGoal.invalidatePlan();
			}
			
			ticksPickingUp++;
			if (ticksPickingUp >= ticksPickingUpMax) {
				ticksPickingUp = 0;
				//return fail();
				state = State.PATHING;
			}
		} else {
		
			BlockLocation loc = UtilMemory.getClosestBlock(block, meta);
			
			if (loc != null) {
				double dist = VecUtil.getDistSqrd(player.getPos(), loc.getPos());
				if (dist < 5) {
					//break block
					//this.playerController.clickBlock(i, j, k, this.objectMouseOver.sideHit);
					//Corobot.playerAI.bridgePlayer.getPlayer();
					
					int x = MathHelper.floor_double(loc.getPos().x);
					int y = MathHelper.floor_double(loc.getPos().y);
					int z = MathHelper.floor_double(loc.getPos().z);
					Block block = worldMC.getBlock(x, y, z);
					if (block == Blocks.air) {
						
						HelperBlock.removeEntry(bb.getWorldMemory(), loc);
						
						ticksMining = 0;
						state = State.PICKINGUP;
						
					} else {
						state = State.MINING;
						
						//this code only works if i open gui chat, why? aim?
						
						//Minecraft.getMinecraft().playerController.clickBlock(x, y, z, 2);
						EntityPlayer playerEnt = Corobot.getPlayerAI().bridgePlayer.getPlayer();
						//TODO: make this adapt to other tools
						//TODO: something to transfer best tool to hotbar if its not in hotbar
						if (!HelperItemUsing.isUsing(ItemUse.FOOD)) {
							int bestSlot = UtilPlayer.getBestToolSlot(ItemPickaxe.class, playerEnt, playerEnt.inventory);
							if (bestSlot != -1) {
								playerEnt.inventory.currentItem = bestSlot;
							}
							Minecraft.getMinecraft().playerController.onPlayerDamageBlock(x, y, z, 2);
							Corobot.getPlayerAI().bridgePlayer.getPlayer().swingItem();
							//Minecraft.getMinecraft().playerController.clickBlock(x, y, z, 2);
							
							ticksMining++;
							if (ticksMining >= ticksMiningMax) {
								return fail();
							}
						}
					}
				} else {
					state = State.PATHING;
					if (world.getTicksTotal() % 40 == 0) {
						getBlackboard().setMoveToBest(loc.getPos());
					}
					ticksPathing++;
					if (ticksPathing >= ticksPathingMax) {
						return fail();
					}
				}
				
				if (alwaysLook) {
					UtilEnt.facePos(Corobot.playerAI.bridgePlayer.getPlayer(), loc.getPos(), lookSpeed, 90);
					Corobot.playerAI.bridgePlayer.getPlayer().rotationPitch += 30;
				}
				
				//Corobot.dbg("state: " + state);
			} else {
				System.out.println("cant find block to mine");
				return EnumBehaviorState.FAILURE;
				//return fail();
			}
		}

		//TODO: see Corobot.java notes on refactor for return states
		if (isTaskComplete()) {
			return EnumBehaviorState.SUCCESS;
		} else {
			return EnumBehaviorState.RUNNING;
		}
	}
	
	public EnumBehaviorState fail() {
		//Corobot.getPlayerAI().planGoal.invalidatePlan();
		return EnumBehaviorState.FAILURE;
	}
	
	@Override
	public void reset() {
		super.reset();
		ticksPathing = 0;
	}
	
	public boolean isTaskComplete() {
		//ItemStack stack = new ItemStack(this.block, this.countNeeded);
		return UtilInventory.getItemCount(Corobot.playerAI.bridgePlayer.getPlayer().inventory, droppedItem/*Item.getItemFromBlock(this.block)*/) >= this.countNeeded;
	}

}
