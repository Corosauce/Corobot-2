package corobot.ai.minigoap.plans;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;

import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.minigoap.IWorldStateProperty;
import com.corosus.ai.minigoap.PlanPiece;

import corobot.ai.memory.pieces.ItemEntry;
import corobot.ai.memory.pieces.inventory.InventorySourceSelf;

public class PlanCraftRecipe extends PlanPiece {

	//example class, realistically there will be a generic craft plan that uses runtime mc data to know how to make a recipe
	private ItemStack itemToCraft;
	
	public PlanCraftRecipe(String planName, IRecipe recipe, List<ItemStack> recipeNeeds) {
		super(planName);
		itemToCraft = recipe.getRecipeOutput();
		this.getEffects().getProperties().add(new ItemEntry(itemToCraft, new InventorySourceSelf()));
		for (ItemStack stack : recipeNeeds) {
			if (stack != null) {
				this.getPreconditions().getProperties().add(new ItemEntry(stack, new InventorySourceSelf()));
			}
		}
	}
	
	public PlanCraftRecipe(PlanPiece obj) {
		super(obj);
		
		IWorldStateProperty effect = obj.getEffects().getProperties().get(0);
		
		//this.getEffects().getProperties().add(obj.getEffects().getProperties().get(0));
		if (effect instanceof ItemEntry) {
			ItemEntry entry = (ItemEntry) effect;
			itemToCraft = entry.getStack();
		}
	}
	
	@Override
	public void initTask(PlanPiece piece, IWorldStateProperty effectRequirement) {
		super.initTask(piece, effectRequirement);
		
		
		
	}
	
	public ItemStack getItemToCraft() {
		return itemToCraft;
	}

	public void setItemToCraft(ItemStack itemToCraft) {
		this.itemToCraft = itemToCraft;
	}

	@Override
	public EnumBehaviorState tick() {
		
		//move to location of workbench
		//right click bench
		//wait for open gui
		//do gui slot work
		
		return super.tick();
	}

}
