package corobot.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.corosus.ai.minigoap.PlanRegistry;

import corobot.ai.minigoap.plans.PlanCraftRecipe;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class UtilRecipe {
	
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

	public static void addRecipePlans() {
		int recipeCount = 0;
		List listt = CraftingManager.getInstance().getRecipeList();
		for (Object obj : CraftingManager.getInstance().getRecipeList()) {
			IRecipe recipeInt = (IRecipe) obj;
			int width = -1;
			int height = -1;
			
			/*if (recipeInt.getRecipeOutput().getDisplayName().contains("Bucket")) {
				int what = 0;
			}*/
			
			List<ItemStack> listRecipeNeeds = new ArrayList<ItemStack>();
			if (recipeInt instanceof ShapedRecipes) {
				ShapedRecipes recipe = (ShapedRecipes) recipeInt;
				width = recipe.recipeWidth;
				height = recipe.recipeHeight;
				for (Object objStacks : recipe.recipeItems) {
					listRecipeNeeds.add((ItemStack) objStacks);
				}
			} else if (recipeInt instanceof ShapelessRecipes) {
				ShapelessRecipes recipe = (ShapelessRecipes) recipeInt;
				for (Object objStacks : recipe.recipeItems) {
					listRecipeNeeds.add((ItemStack) objStacks);
				}
			} else if (recipeInt instanceof ShapedOreRecipe) {
				ShapedOreRecipe recipe = (ShapedOreRecipe) recipeInt;
				width = ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, recipe, "width");
				height = ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, recipe, "height");
				/*for (int i = 0; i < recipe.getInput().length; i++) {
					Object objList = recipe.getInput()[i];*/
				for (Object objList : recipe.getInput()) {
					if (objList instanceof Collection) {
						ArrayList list = (ArrayList) objList;
						//listRecipeNeeds = new ArrayList<ItemStack>();
						if (list != null) {
							for (Object objStacks : list) {
								listRecipeNeeds.add((ItemStack) objStacks);
								
								//TODO: FOR NOW LETS ONLY ADD FIRST ENTRY, shouldnt break too much.... see notes at top of class
								break;
							}
							/*if (listRecipeNeeds.size() > 0) {
								System.out.println("adding multi plan for recipe: " + recipeInt.getRecipeOutput().getDisplayName());
								PlanRegistry.addPlanPiece(new PlanCraftRecipe(recipeInt.getRecipeOutput().getDisplayName() + recipeCount++, recipeInt, listRecipeNeeds));
							}*/
							//listRecipeNeeds.clear();
						}
					} else /*if (objList instanceof ItemStack)*/{
						listRecipeNeeds.add((ItemStack) objList);
					}
				}
			} else if (recipeInt instanceof ShapelessOreRecipe) {
				ShapelessOreRecipe recipe = (ShapelessOreRecipe) recipeInt;
				for (Object objList : recipe.getInput()) {
					if (objList instanceof Collection) {
						ArrayList list = (ArrayList) objList;
						
						if (list != null) {
							for (Object objStacks : list) {
								listRecipeNeeds.add((ItemStack) objStacks);
								
								//TODO: FOR NOW LETS ONLY ADD FIRST ENTRY, shouldnt break too much.... see notes at top of class
								break;
							}
						}
					} else if (objList instanceof ItemStack){
						listRecipeNeeds.add((ItemStack) objList);
					}
				}
			} else {
				System.out.println("not handling: " + recipeInt);
			}
			
			if (listRecipeNeeds.size() > 0) {
				String recipeName = recipeInt.getRecipeOutput().getDisplayName() + recipeCount++;
				if (recipeName.contains("Bucket")) {
					int sdfdf = 0;
				}
				System.out.println("adding plan for recipe: " + recipeName + " - " + width + "x" + height + " - " + recipeInt.getRecipeOutput() + " using items " + listRecipeNeeds);
				PlanRegistry.addPlanPiece(new PlanCraftRecipe(recipeName, recipeInt, listRecipeNeeds, width, height));
			}
		}
	}
	
}
