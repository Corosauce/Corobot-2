package corobot.ai.minigoap.plans;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.corosus.ai.minigoap.PlanPiece;

import corobot.ai.memory.pieces.ItemEntry;
import corobot.ai.memory.pieces.ResourceLocation;
import corobot.ai.memory.pieces.inventory.InventorySourceSelf;

public class PlanMineBlock extends PlanPiece {

	//should be close to location be a precondition?
	//should we add a move to location plan? might be a good idea
	
	public PlanMineBlock(String planName, Block block) {
		super(planName);
		this.getEffects().getProperties().add(new ItemEntry(new ItemStack(block), new InventorySourceSelf()));
		this.getPreconditions().getProperties().add(new ResourceLocation(null, block));
	}
	
	public PlanMineBlock(String planName, ItemStack itemReturned, Block block) {
		super(planName);
		this.getEffects().getProperties().add(new ItemEntry(itemReturned, new InventorySourceSelf()));
		this.getPreconditions().getProperties().add(new ResourceLocation(null, block));
	}
	
	@Override
	public void tickTask() {
		super.tickTask();
		
		//get closest mineable log
		//path to near location
		//mining sequence
		//search for item drop to grab
		//mine another if unable to grab
		//confirm grabbed log
		//complete
	}

}
