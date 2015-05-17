package corobot.ai.minigoap.plans;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.corosus.ai.AIBTAgent;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.minigoap.IWorldStateProperty;
import com.corosus.ai.minigoap.PlanPiece;
import com.corosus.entity.IEntity;
import com.corosus.util.VecUtil;
import com.corosus.world.IWorld;

import corobot.Corobot;
import corobot.ai.memory.pieces.BlockLocation;
import corobot.ai.memory.pieces.ItemEntry;
import corobot.ai.memory.pieces.inventory.InventorySourceSelf;
import corobot.util.UtilContainer;
import corobot.util.UtilInventory;
import corobot.util.UtilMemory;

public class PlanCraftRecipe extends PlanPiece {

	//example class, realistically there will be a generic craft plan that uses runtime mc data to know how to make a recipe
	private ItemStack itemToCraft;
	
	public State state = State.PATHING;
	
	public enum State {
		PATHING, WAITING_ON_GUI, GUI_OPEN;
	}
	
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
		
		int sizeCraftGrid = 9;
		int sizeInventoryMain = 27;
		int sizeInventoryHotbar = 9;
		
		int slotCraftOut = 0;
		int slotCraftMatrixStart = 1;
		int slotInventoryMainStart = 1+sizeCraftGrid;
		int slotInventoryHotbarStart = slotInventoryMainStart+sizeInventoryMain;
		
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		EntityPlayer playerEnt = Corobot.getPlayerAI().bridgePlayer.getPlayer();
		
		BlockLocation loc = UtilMemory.getClosestBlock(Blocks.crafting_table);
		
		if (loc != null) {
			double dist = VecUtil.getDistSqrd(player.getPos(), loc.getPos());
			if (dist < 3) {
				
				World worldMC = Minecraft.getMinecraft().theWorld;
				int x = MathHelper.floor_double(loc.getPos().x);
				int y = MathHelper.floor_double(loc.getPos().y);
				int z = MathHelper.floor_double(loc.getPos().z);
				Block block = worldMC.getBlock(x, y, z);
				
				if (playerEnt.openContainer instanceof ContainerWorkbench) {
					System.out.println("slot click");
					UtilContainer.clickSlot(slotInventoryHotbarStart);
					UtilContainer.clickSlot(slotInventoryHotbarStart+1);
					UtilContainer.clickSlot(slotInventoryHotbarStart+2);
				} else {
					System.out.println("open gui");
					UtilContainer.openContainer(x, y, z);
				}
				
			} else {
				state = State.PATHING;
				if (world.getTicksTotal() % 40 == 0) {
					player.setMoveTo(loc.getPos());
				}
			}
			Corobot.dbg("state: " + state);
		} else {
			System.out.println("cant find crafting table");
		}
		
		return super.tick();
	}
	
	@Override
	public boolean isTaskComplete() {
		//TODO: meta / stacksize
		return UtilInventory.getItemCount(Corobot.playerAI.bridgePlayer.getPlayer().inventory, itemToCraft.getItem()) > 0;
	}

}
