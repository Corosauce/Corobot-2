package corobot.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.corosus.ai.minigoap.PlanRegistry;

import corobot.ai.minigoap.plans.PlanCraftRecipe;
import corobot.ai.minigoap.plans.PlanSmeltRecipe;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class UtilFurnace {
	
	/**
	 * Carefull with ShapedOreRecipe and ShapelessOreRecipe
	 * 
	 * Forge arrayifies what would be assumed itemstack entries, I assume this is so the ore dict can add multiple types of ores allowed for this recipe
	 * 
	 * For a vanilla setup, these arrays only have 1 entry, so FOR NOW we can just add that entry as an itemstack, but....
	 * 
	 * If there were multiple itemstacks in that array, we will be parsing this data wrong, we'd need to tell our GOAP system that corobot can mix and match these materials
	 * 
	 * keep this in mind when implementing actual recipe use in future
	 */

	public static void addFurnacePlans() {
		int recipeCount = 0;
		
		Map list = FurnaceRecipes.smelting().getSmeltingList();
		for (Object obj : list.entrySet()) {
			Map.Entry entry = (Entry) obj;
			ItemStack stackFrom = (ItemStack) entry.getKey();
			ItemStack stackTo = (ItemStack) entry.getValue();
			System.out.println("adding plan for smelting: " + stackFrom.getDisplayName() + " -> " + stackTo.getDisplayName() + recipeCount);
			PlanRegistry.addPlanPiece(new PlanSmeltRecipe("Smelt " + stackTo.getDisplayName() + recipeCount, stackFrom, stackTo));
			recipeCount++;
		}
	}
	
}
