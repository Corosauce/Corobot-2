package corobot.ai.minigoap.plans;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.minigoap.PlanPiece;

import corobot.ai.memory.PlayerMemoryState;
import corobot.ai.memory.pieces.InventoryCollection;
import corobot.ai.memory.pieces.ItemEntry;
import corobot.ai.memory.pieces.inventory.InventorySourceSelf;

public class PlanCraftRecipeManual extends PlanPiece {

	//example class, realistically there will be a generic craft plan that uses runtime mc data to know how to make a recipe
	//private Item itemToCraft;
	
	public PlanCraftRecipeManual(String planName, ItemStack itemToCraft, ItemStack... itemsNeededToCraft) {
		super(planName);
		//this.itemToCraft = itemToCraft;
		
		/*PlayerMemoryState effect = new PlayerMemoryState();
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		stacks.add(new ItemStack(itemToCraft));
		
		InventoryCollection col = new InventoryCollection(stacks, new InventorySourceSelf());
		effect.listInventories.add(col);
		
		this.setEffects(effect);*/
		
		this.getEffects().getProperties().add(new ItemEntry(itemToCraft, new InventorySourceSelf()));
		for (ItemStack neededItems : itemsNeededToCraft) {
			this.getPreconditions().getProperties().add(new ItemEntry(neededItems, new InventorySourceSelf()));
		}
		
		//TEMP, this will be generated from recipe lookup, might need more complex solution, given multiple recipe possibilities for items, eg, different woods used
		/*this.getPreconditions().getProperties().add(new ItemEntry(new ItemStack(Items.stick, 2), new InventorySourceSelf()));
		this.getPreconditions().getProperties().add(new ItemEntry(new ItemStack(Blocks.planks, 3), new InventorySourceSelf()));*/
		
	}
	
	@Override
	public EnumBehaviorState tick() {
		
		System.out.println("craft recipe manual plan");
		
		//move to location of workbench
		//right click bench
		//wait for open gui
		//do gui slot work
		return super.tick();
	}

}
