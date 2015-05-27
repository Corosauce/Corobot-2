package corobot.ai.minigoap.plans;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
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
import corobot.ai.memory.helper.HelperInventory;
import corobot.ai.memory.pieces.BlockLocation;
import corobot.ai.memory.pieces.ItemEntry;
import corobot.ai.memory.pieces.inventory.InventorySourceSelf;
import corobot.util.UtilContainer;
import corobot.util.UtilInventory;
import corobot.util.UtilMemory;

public class PlanCraftRecipe extends PlanPiece {

	private ItemStack itemToCraft;
	private int amountToCraft = 1;
	
	public State state = State.PATHING;
	public int width = -1;
	public int height = -1;
	public List<ItemStack> listRecipeShape = null;
	
	public static int sizeCraftGrid = 9;
	public static int sizeInventoryMain = 27;
	public static int sizeInventoryHotbar = 9;
	
	public static int slotCraftOut = 0;
	public static int slotCraftMatrixStart = 1;
	public static int slotInventoryMainStart = 1+sizeCraftGrid;
	public static int slotInventoryHotbarStart = slotInventoryMainStart+sizeInventoryMain;
	
	public enum State {
		PATHING, WAITING_ON_GUI, GUI_OPEN;
	}
	
	public PlanCraftRecipe(String planName, Blackboard blackboard, IRecipe recipe, List<ItemStack> recipeNeeds, int width, int height) {
		super(planName, blackboard);
		itemToCraft = recipe.getRecipeOutput();
		this.width = width;
		this.height = height;
		this.listRecipeShape = recipeNeeds;
		this.getEffects().getProperties().add(new ItemEntry(itemToCraft, new InventorySourceSelf()));
		for (ItemStack stack : recipeNeeds) {
			if (stack != null) {
				this.getPreconditions().getProperties().add(new ItemEntry(stack, new InventorySourceSelf()));
			}
		}
	}
	
	public PlanCraftRecipe(PlanPiece obj) {
		super(obj, obj.getBlackboard());
		
		PlanCraftRecipe src = (PlanCraftRecipe) obj;
		
		itemToCraft = src.itemToCraft;
		amountToCraft = src.amountToCraft;
		width = src.width;
		height = src.height;
		listRecipeShape = new ArrayList(src.listRecipeShape);
		
		/*IWorldStateProperty effect = obj.getEffects().getProperties().get(0);
		
		if (effect instanceof ItemEntry) {
			ItemEntry entry = (ItemEntry) effect;
			itemToCraft = entry.getStack();
		}*/
	}
	
	@Override
	public void initTask(PlanPiece piece, IWorldStateProperty effectRequirement) {
		super.initTask(piece, effectRequirement);
		
		
		
	}
	
	public int getAmountToCraft() {
		return amountToCraft;
	}

	public void setAmountToCraft(int amountToCraft) {
		this.amountToCraft = amountToCraft;
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
		
		//trying to fix weird order of execution bug...
		if (isTaskComplete()) return EnumBehaviorState.SUCCESS;
		
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		EntityPlayer playerEnt = Corobot.getPlayerAI().bridgePlayer.getPlayer();
		
		BlockLocation loc = UtilMemory.getClosestBlock(Blocks.crafting_table, -1);
		
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
					
					if (itemToCraft.getItem() == Items.wooden_hoe) {
						int test = 0;
					}
					
					for (int i = 0; i < listRecipeShape.size(); i++) {
						ItemStack stack = listRecipeShape.get(i);
						if (stack != null) {
							int clickFrom = getFirstSlotContainingItem(playerEnt.openContainer, stack, slotInventoryMainStart, slotInventoryHotbarStart+sizeInventoryHotbar);
							System.out.println("clickFrom: " + clickFrom);
							if (clickFrom != -1) {
								int clickTo = slotCraftMatrixStart;
								if (width == -1) {
									//place in first open slot
									//this might work
									clickTo = i;
									System.out.println("clickTo shapeless: " + clickTo);
								} else {
									int iOffset = i;
									//get recipe position from recipe size
									int xx = (iOffset % width)+1;
									int yy = ((int)(iOffset / width));
									
									System.out.println("xx/yy: " + xx + "/" + yy);
									
									//convert to crafting grid size, subtract 1 to reset to undo iOffset, wait no, dont subtract
									clickTo = (((yy) * 3) + xx);
									/*xx -= 1;
									yy -= 1;*/
									
									System.out.println("clickTo: " + clickTo);
									System.out.println("durr");
								}
								

								
								
								UtilContainer.clickSlot(clickFrom, UtilContainer.mouseLeftClick, UtilContainer.mouse2StepClick);
								UtilContainer.clickSlot(clickTo, UtilContainer.mouseRightClick, UtilContainer.mouse2StepClick);
								UtilContainer.clickSlot(clickFrom, UtilContainer.mouseLeftClick, UtilContainer.mouse2StepClick);
							} else {
								System.out.println("CRITICAL! failed to find item " + stack + ", did something remove it since plan was made?");
								Corobot.getPlayerAI().planGoal.invalidatePlan();
								endTask();
							}
						}
						
					}


					
					UtilContainer.clickSlot(slotCraftOut, UtilContainer.mouseLeftClick, UtilContainer.mouseShiftClick);
					System.out.println("slot click complete");
					endTask();
					
					/*UtilContainer.clickSlot(slotInventoryHotbarStart);
					UtilContainer.clickSlot(slotInventoryHotbarStart+1);
					UtilContainer.clickSlot(slotInventoryHotbarStart+2);*/
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
			//Corobot.dbg("state: " + state);
		} else {
			System.out.println("cant find crafting table");
			Corobot.getPlayerAI().planGoal.invalidatePlan();
			endTask();
		}
		
		return super.tick();
	}
	
	@Override
	public boolean isTaskComplete() {
		return UtilInventory.getItemCount(Corobot.playerAI.bridgePlayer.getPlayer().inventory, itemToCraft) >= amountToCraft;
	}
	
	public static int getFirstSlotContainingItem(Container container, ItemStack itemStack, int findStart, int findEnd) {
		int index = -1;
		
		for (int i = findStart; i < findEnd; i++) {
			ItemStack stack = (ItemStack) ((Slot)container.inventorySlots.get(i)).getStack();
			
			if (UtilInventory.isSame(itemStack, stack)) {
				index = i;
				break;
			}
		}
		
		return index;
	}
	
	@Override
	public boolean canPlanBeUsedMultipleTimes() {
		return false;
	}
	
	@Override
	public void endTask() {
		for (int i = 0; i < 9; i++) {
			UtilContainer.clickSlot(slotCraftMatrixStart+i, UtilContainer.mouseLeftClick, UtilContainer.mouseShiftClick);
		}
		EntityPlayer playerEnt = Corobot.getPlayerAI().bridgePlayer.getPlayer();
		if (Minecraft.getMinecraft().currentScreen instanceof GuiContainer) {
			((GuiContainer)Minecraft.getMinecraft().currentScreen).inventorySlots.onContainerClosed(playerEnt);
		}
		Minecraft.getMinecraft().displayGuiScreen(new GuiChat(""));
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		HelperInventory.updateCache(agent.getBlackboard().getWorldMemory(), HelperInventory.selfInventory, Corobot.playerAI.bridgePlayer.getPlayer().inventory);
	}

}
