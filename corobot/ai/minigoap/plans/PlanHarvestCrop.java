package corobot.ai.minigoap.plans;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.minigoap.PlanPiece;

import corobot.ai.memory.pieces.ItemEntry;
import corobot.ai.memory.pieces.ResourceLocation;
import corobot.ai.memory.pieces.inventory.InventorySourceSelf;

public class PlanHarvestCrop extends PlanPiece {
	
	public PlanHarvestCrop(String planName, ItemStack stackEffect, ItemStack stackPrecond) {
		super(planName);
		
		this.getEffects().getProperties().add(new ItemEntry(stackEffect, new InventorySourceSelf()));
		this.getPreconditions().getProperties().add(new ItemEntry(stackPrecond, new InventorySourceSelf()));
	}
	
	public PlanHarvestCrop(PlanPiece obj) {
		super(obj);
	}
	
	@Override
	public EnumBehaviorState tick() {
		
		
		
		return super.tick();
	}

}
