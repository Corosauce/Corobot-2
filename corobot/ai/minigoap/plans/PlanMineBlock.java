package corobot.ai.minigoap.plans;

import java.util.List;

import javax.vecmath.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
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
import corobot.ai.memory.pieces.BlockLocation;
import corobot.ai.memory.pieces.ItemEntry;
import corobot.ai.memory.pieces.ResourceLocation;
import corobot.ai.memory.pieces.inventory.InventorySourceSelf;
import corobot.util.UtilEnt;
import corobot.util.UtilInventory;
import corobot.util.UtilMemory;

public class PlanMineBlock extends PlanPiece {

	//should be close to location be a precondition?
	
	//should we add a move to location plan? might be a good idea
	//should we add a pickup item plan?
	
	//how does item counts factor into this?
	//- for now just do 1 and see where it goes, but some recipes will require more than 1.....
	//- needs a sort of dynamic plan that can pass requirements along to other plans, fuzzy conditions ?
	
	public Block block;
	public int meta;
	public ItemStack neededTool = null;
	public State state = State.PATHING;
	public int countNeeded = 1;
	public int ticksPickingUp = 0;
	public int ticksPickingUpMax = 80;
	public int ticksMining = 0;
	public int ticksMiningMax = 120;
	public int ticksPathing = 0;
	public int ticksPathingMax = 200;
	
	public enum State {
		PATHING, MINING, PICKINGUP;
	}
	
	public PlanMineBlock(String planName, Block block, int meta, ItemStack tool) {
		super(planName);
		this.block = block;
		this.meta = meta;
		this.neededTool = tool;
		this.getEffects().getProperties().add(new ItemEntry(new ItemStack(block), new InventorySourceSelf()));
		this.getPreconditions().getProperties().add(new ResourceLocation(null, block, meta));
	}
	
	public PlanMineBlock(String planName, ItemStack itemReturned, Block block, int meta, ItemStack tool) {
		super(planName);
		this.block = block;
		this.meta = meta;
		this.neededTool = tool;
		this.getEffects().getProperties().add(new ItemEntry(itemReturned, new InventorySourceSelf()));
		this.getPreconditions().getProperties().add(new ResourceLocation(null, block, meta));
	}
	
	public PlanMineBlock(PlanPiece obj) {
		super(obj);
		block = ((PlanMineBlock)obj).block;
		meta = ((PlanMineBlock)obj).meta;
		neededTool = ((PlanMineBlock)obj).neededTool;
		countNeeded = ((PlanMineBlock)obj).countNeeded;
	}
	
	@Override
	public EnumBehaviorState tick() {
		
		//System.out.println("mine block plan");
		
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		Blackboard bb = agent.getBlackboard();
		
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
		
		BlockLocation loc = UtilMemory.getClosestBlock(block, meta);
		
		if (loc != null) {
			double dist = VecUtil.getDistSqrd(player.getPos(), loc.getPos());
			if (dist < 5) {
				//break block
				//this.playerController.clickBlock(i, j, k, this.objectMouseOver.sideHit);
				//Corobot.playerAI.bridgePlayer.getPlayer();
				World worldMC = Minecraft.getMinecraft().theWorld;
				int x = MathHelper.floor_double(loc.getPos().x);
				int y = MathHelper.floor_double(loc.getPos().y);
				int z = MathHelper.floor_double(loc.getPos().z);
				Block block = worldMC.getBlock(x, y, z);
				if (block == Blocks.air) {
					bb.getWorldMemory().getProperties().remove(loc);
					state = State.PICKINGUP;
					EntityItem closestItem = UtilEnt.getClosestItem(worldMC, player.getPos(), Item.getItemFromBlock(this.block));
					if (closestItem != null) {
						if (world.getTicksTotal() % 40 == 0) {
							player.setMoveTo(new Vector3f((float)closestItem.posX, (float)closestItem.posY, (float)closestItem.posZ));
						}
						ticksPickingUp++;
						if (ticksPickingUp >= ticksPickingUpMax) {
							Corobot.getPlayerAI().planGoal.invalidatePlan();
						}
					} else {
						Corobot.getPlayerAI().planGoal.invalidatePlan();
					}
					
				} else {
					state = State.MINING;
					
					//this code only works if i open gui chat, why? aim?
					
					//Minecraft.getMinecraft().playerController.clickBlock(x, y, z, 2);
					Minecraft.getMinecraft().playerController.onPlayerDamageBlock(x, y, z, 2);
					Corobot.getPlayerAI().bridgePlayer.getPlayer().swingItem();
					//Minecraft.getMinecraft().playerController.clickBlock(x, y, z, 2);
					
					ticksMining++;
					if (ticksMining >= ticksMiningMax) {
						Corobot.getPlayerAI().planGoal.invalidatePlan();
					}
				}
			} else {
				state = State.PATHING;
				if (world.getTicksTotal() % 40 == 0) {
					player.setMoveTo(loc.getPos());
				}
				ticksPathing++;
				if (ticksPathing >= ticksPathingMax) {
					Corobot.getPlayerAI().planGoal.invalidatePlan();
				}
			}
			//Corobot.dbg("state: " + state);
		} else {
			System.out.println("cant find block to mine");
			Corobot.getPlayerAI().planGoal.invalidatePlan();
		}
		
		//get closest mineable log
		//path to near location
		//mining sequence
		//search for item drop to grab
		//mine another if unable to grab
		//confirm grabbed log
		//complete
		
		return super.tick();
	}
	
	@Override
	public boolean isTaskComplete() {
		ItemStack stack = new ItemStack(this.block, this.countNeeded);
		return UtilInventory.getItemCount(Corobot.playerAI.bridgePlayer.getPlayer().inventory, stack/*Item.getItemFromBlock(this.block)*/) >= this.countNeeded;
	}

}
