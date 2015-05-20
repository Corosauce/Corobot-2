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
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
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
import corobot.ai.memory.helper.HelperInventory;
import corobot.ai.memory.pieces.BlockLocation;
import corobot.ai.memory.pieces.ItemEntry;
import corobot.ai.memory.pieces.inventory.InventorySourceSelf;
import corobot.util.UtilContainer;
import corobot.util.UtilInventory;
import corobot.util.UtilMemory;

public class PlanSmeltRecipe extends PlanPiece {

	/**
	 * Until we have a way to do preconditions with OR clauses, we will just use coal as a fuel, no others
	 * 
	 * make sure plan isnt invalided when cache updates while items are in furnace container
	 */
	private ItemStack itemTo;
	private ItemStack itemFrom;
	private int amountToSmelt = 1;
	
	public State state = State.PATHING;
	
	public static int sizeInventoryMain = 27;
	public static int sizeInventoryHotbar = 9;
	
	public static int slotSmeltIn = 0;
	public static int slotSmeltFuel = 1;
	public static int slotSmeltOut = 2;
	public static int slotInventoryMainStart = slotSmeltOut+1;
	public static int slotInventoryHotbarStart = slotInventoryMainStart+sizeInventoryMain;
	
	public enum State {
		PATHING, WAITING_ON_GUI, GUI_OPEN, WAITING_ON_SMELT;
	}
	
	public PlanSmeltRecipe(String planName, ItemStack itemFrom, ItemStack itemTo) {
		super(planName);
		this.itemFrom = itemFrom;
		this.itemTo = itemTo;
		this.getEffects().getProperties().add(new ItemEntry(itemTo, new InventorySourceSelf()));
		//need 5 coal too
		//stacksize of 5 broke precondition since mining coal only has effect of 1 stacksize
		this.getPreconditions().getProperties().add(new ItemEntry(new ItemStack(Items.coal), new InventorySourceSelf()));
		this.getPreconditions().getProperties().add(new ItemEntry(itemFrom, new InventorySourceSelf()));
	}
	
	public PlanSmeltRecipe(PlanPiece obj) {
		super(obj);
		
		PlanSmeltRecipe src = (PlanSmeltRecipe) obj;
		
		itemTo = src.itemTo;
		itemFrom = src.itemFrom;
		amountToSmelt = src.amountToSmelt;
	}
	
	@Override
	public void initTask(PlanPiece piece, IWorldStateProperty effectRequirement) {
		super.initTask(piece, effectRequirement);
		
		
		
	}
	
	public int getAmountToCraft() {
		return amountToSmelt;
	}

	public void setAmountToCraft(int amountToCraft) {
		this.amountToSmelt = amountToCraft;
	}

	public ItemStack getItemToCraft() {
		return itemTo;
	}

	public void setItemToCraft(ItemStack itemToCraft) {
		this.itemTo = itemToCraft;
	}

	@Override
	public EnumBehaviorState tick() {
		
		//move to location of workbench
		//right click bench
		//wait for open gui
		//do gui slot work
		
		
		
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		EntityPlayer playerEnt = Corobot.getPlayerAI().bridgePlayer.getPlayer();
		
		BlockLocation loc = UtilMemory.getClosestBlock(Blocks.furnace, -1);
		
		if (loc == null) {
			loc = UtilMemory.getClosestBlock(Blocks.lit_furnace, -1);
		}
		
		if (loc != null) {
			double dist = VecUtil.getDistSqrd(player.getPos(), loc.getPos());
			if (dist < 3) {
				
				World worldMC = Minecraft.getMinecraft().theWorld;
				int x = MathHelper.floor_double(loc.getPos().x);
				int y = MathHelper.floor_double(loc.getPos().y);
				int z = MathHelper.floor_double(loc.getPos().z);
				Block block = worldMC.getBlock(x, y, z);
				
				if (playerEnt.openContainer instanceof ContainerFurnace) {
					//System.out.println("slot click");
					ContainerFurnace furnaceContainer = (ContainerFurnace) playerEnt.openContainer;
					
					if (itemTo.getItem() == Items.wooden_hoe) {
						int test = 0;
					}
					
					//clean out furnace incase its being used
					if (state == State.WAITING_ON_GUI) {
						UtilContainer.clickSlot(slotSmeltIn, UtilContainer.mouseLeftClick, UtilContainer.mouseShiftClick);
						UtilContainer.clickSlot(slotSmeltFuel, UtilContainer.mouseLeftClick, UtilContainer.mouseShiftClick);
						UtilContainer.clickSlot(slotSmeltOut, UtilContainer.mouseLeftClick, UtilContainer.mouseShiftClick);
					}
					
					if (state != State.WAITING_ON_SMELT) {
						state = State.GUI_OPEN;
					}
					
					if (state == State.GUI_OPEN) {
						int clickFrom = getFirstSlotContainingItem(playerEnt.openContainer, itemFrom, slotInventoryMainStart, slotInventoryHotbarStart+sizeInventoryHotbar);
						if (clickFrom != -1) {
							int clickTo = slotSmeltIn;
							
							//check for coal to add
							//- itemstack is consumed to provide time to burn
							//- i guess we will make sure we have 1 more coal ready to go once its needed
							boolean needFuel = false;
							ItemStack stackFuel = furnaceContainer.getSlot(slotSmeltFuel).getStack();
							if (stackFuel == null) {
								needFuel = true;
							} else {
								
							}
							
							if (needFuel) {
								// will place 5 coal at a time
								// cases to account for:
								//- none, since we expect a null fuel slot, so we cant overfill and have item in cursor hand afterwards
								//-- this might become issue in future if we optimize fuel management
								ItemStack stackFuelToFind = new ItemStack(Items.coal);
								int clickFromFuel = getFirstSlotContainingItem(playerEnt.openContainer, stackFuelToFind, slotInventoryMainStart, slotInventoryHotbarStart+sizeInventoryHotbar);
								if (clickFromFuel != -1) {
									//pickup stack and place whole stack in
									UtilContainer.clickSlot(clickFromFuel, UtilContainer.mouseLeftClick, UtilContainer.mouseShiftClick);
									//UtilContainer.clickSlot(slotSmeltFuel, UtilContainer.mouseLeftClick, UtilContainer.mouse2StepClick);
								} else {
									System.out.println("cant find coal for fuel, error");
									Corobot.getPlayerAI().planGoal.invalidatePlan();
									endTask();
								}
							}
							
							//finally smelt
							//pickup stack and place whole stack in
							UtilContainer.clickSlot(clickFrom, UtilContainer.mouseLeftClick, UtilContainer.mouseShiftClick);
							//UtilContainer.clickSlot(slotSmeltFuel, UtilContainer.mouseLeftClick, UtilContainer.mouse2StepClick);
							
							state = State.WAITING_ON_SMELT;
						} else {
							System.out.println("cant find itemFrom to smelt, error");
							Corobot.getPlayerAI().planGoal.invalidatePlan();
							endTask();
						}
					} else if (state == State.WAITING_ON_SMELT) {
						//check for smelted stuff, then switch to gui open, at which point isComplete might be true and end this class
						
						ItemStack stackOut = furnaceContainer.getSlot(slotSmeltOut).getStack();
						
						if (stackOut != null) {
							//UtilContainer.clickSlot(slotSmeltOut, UtilContainer.mouseLeftClick, UtilContainer.mouseShiftClick);
							
							UtilContainer.clickSlot(slotSmeltIn, UtilContainer.mouseLeftClick, UtilContainer.mouseShiftClick);
							UtilContainer.clickSlot(slotSmeltFuel, UtilContainer.mouseLeftClick, UtilContainer.mouseShiftClick);
							UtilContainer.clickSlot(slotSmeltOut, UtilContainer.mouseLeftClick, UtilContainer.mouseShiftClick);
							state = State.GUI_OPEN;
							System.out.println("smelted!");
						} else {
							
							System.out.println("waiting for smelting");
							ItemStack stackCheck = furnaceContainer.getSlot(slotSmeltIn).getStack();
							if (stackCheck == null) {
								System.out.println("CRITICAL! item actively smelting missing for some reason");
								Corobot.getPlayerAI().planGoal.invalidatePlan();
								endTask();
							}
						}
					}
				} else {
					System.out.println("open gui");
					state = State.WAITING_ON_GUI;
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
		return UtilInventory.getItemCount(Corobot.playerAI.bridgePlayer.getPlayer().inventory, itemTo) >= amountToSmelt;
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
		/*for (int i = 0; i < 9; i++) {
			UtilContainer.clickSlot(slotCraftMatrixStart+i, UtilContainer.mouseLeftClick, UtilContainer.mouseShiftClick);
		}*/
		EntityPlayer playerEnt = Corobot.getPlayerAI().bridgePlayer.getPlayer();
		if (Minecraft.getMinecraft().currentScreen instanceof GuiContainer) {
			((GuiContainer)Minecraft.getMinecraft().currentScreen).inventorySlots.onContainerClosed(playerEnt);
		}
		Minecraft.getMinecraft().displayGuiScreen(new GuiChat(""));
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		HelperInventory.updateCache(agent.getBlackboard().getWorldMemory(), HelperInventory.selfInventory, Corobot.playerAI.bridgePlayer.getPlayer().inventory);
	}

}
