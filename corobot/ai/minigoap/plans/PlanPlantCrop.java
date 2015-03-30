package corobot.ai.minigoap.plans;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.corosus.ai.minigoap.PlanPiece;

import corobot.ai.memory.pieces.ItemEntry;
import corobot.ai.memory.pieces.ResourceLocation;
import corobot.ai.memory.pieces.inventory.InventorySourceSelf;

public class PlanPlantCrop extends PlanPiece {
	
	public PlanPlantCrop(String planName, Block blockEffect, ItemStack seedsToUse) {
		super(planName);
		
		this.getEffects().getProperties().add(new ItemEntry(new ItemStack(blockEffect), new InventorySourceSelf()));
		this.getPreconditions().getProperties().add(new ItemEntry(new ItemStack(Blocks.farmland), new InventorySourceSelf()));
		this.getPreconditions().getProperties().add(new ItemEntry(seedsToUse, new InventorySourceSelf()));
	}
	
	@Override
	public void tickTask() {
		super.tickTask();
	}

}
