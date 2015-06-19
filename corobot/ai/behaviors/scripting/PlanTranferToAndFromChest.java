package corobot.ai.behaviors.scripting;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
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
import corobot.util.UtilContainer;
import corobot.util.UtilEnt;
import corobot.util.UtilInventory;
import corobot.util.UtilMemory;

public class PlanTranferToAndFromChest extends PlanPiece {
	
	public State state = State.PATHING;
	
	public static int sizeInventoryChest = 27;
	public static int sizeInventoryMain = 27;
	public static int sizeInventoryHotbar = 9;
	
	public static int slotInventoryMainStart = 1+sizeInventoryChest;
	public static int slotInventoryHotbarStart = slotInventoryMainStart+sizeInventoryMain;
	
	public List<ItemStack> listItemsToTake = new ArrayList<ItemStack>();
	public List<ItemStack> listItemsToAdd = new ArrayList<ItemStack>();
	public Vector3f posChest;
	public int ticksPathing = 0;
	public int ticksPathingMax = 300;
	
	public enum State {
		PATHING, WAITING_ON_GUI, GUI_OPEN;
	}
	
	public PlanTranferToAndFromChest(String planName, Blackboard blackboard, Vector3f posChest, List<ItemStack> itemsToTake, List<ItemStack> itemsToAdd) {
		super(planName, blackboard);
		
		if (itemsToAdd != null) this.listItemsToAdd = itemsToAdd;
		if (itemsToTake != null) this.listItemsToTake = itemsToTake;
		this.posChest = posChest;
	}
	
	@Override
	public void initTask(PlanPiece piece, IWorldStateProperty effectRequirement, IWorldStateProperty preconditionRequirement) {
		super.initTask(piece, effectRequirement, preconditionRequirement);
		
		
		
	}

	@Override
	public EnumBehaviorState tick() {
		
		//move to location of workbench
		//right click bench
		//wait for open gui
		//do gui slot work
		
		//trying to fix weird order of execution bug...
		//if (isTaskComplete()) return EnumBehaviorState.SUCCESS;
		
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		EntityPlayer playerEnt = Corobot.getPlayerAI().bridgePlayer.getPlayer();
		
		//BlockLocation loc = UtilMemory.getClosestBlock(Blocks.crafting_table, -1);
		
		
		//if (loc != null) {
			double dist = VecUtil.getDistSqrd(player.getPos(), posChest);
			if (dist < 3) {
				
				World worldMC = Minecraft.getMinecraft().theWorld;
				int x = MathHelper.floor_double(posChest.x);
				int y = MathHelper.floor_double(posChest.y);
				int z = MathHelper.floor_double(posChest.z);
				Block block = worldMC.getBlock(x, y, z);
				
				if (playerEnt.openContainer instanceof ContainerChest) {
					System.out.println("slot click");
					
					for (int i = 0; i < listItemsToAdd.size(); i++) {
						ItemStack stack = listItemsToAdd.get(i);
						if (stack != null) {
							int clickFrom = getFirstSlotContainingItem(playerEnt.openContainer, stack, slotInventoryMainStart, slotInventoryHotbarStart+sizeInventoryHotbar-1);
							System.out.println("clickFrom: " + clickFrom);
							
							if (clickFrom != -1) {
								UtilContainer.clickSlot(clickFrom, UtilContainer.mouseLeftClick, UtilContainer.mouseShiftClick);
								//UtilContainer.clickSlot(clickFrom+1, UtilContainer.mouseLeftClick, UtilContainer.mouseShiftClick);
								//UtilContainer.clickSlot(clickFrom-1, UtilContainer.mouseLeftClick, UtilContainer.mouseShiftClick);
							}
						}
					}
					
					for (int i = 0; i < listItemsToTake.size(); i++) {
						ItemStack stack = listItemsToTake.get(i);
						if (stack != null) {
							int clickFrom = getFirstSlotContainingItem(playerEnt.openContainer, stack, 0, sizeInventoryChest);
							System.out.println("clickFrom: " + clickFrom);
							
							if (clickFrom != -1) {
								UtilContainer.clickSlot(clickFrom, UtilContainer.mouseLeftClick, UtilContainer.mouseShiftClick);
							}
						}
					}
					return EnumBehaviorState.SUCCESS;
				} else {
					System.out.println("open gui");
					UtilContainer.openContainer(x, y, z);
					//gui slot click code happens too fast, need delay
					try {
						Thread.sleep(50);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				
			} else {
				state = State.PATHING;
				if (world.getTicksTotal() % 40 == 0) {
					getBlackboard().setMoveToBest(posChest);
				}
				
				boolean alwaysLook = true;
				int lookSpeed = 5;
				if (alwaysLook) {
					UtilEnt.facePos(Corobot.playerAI.bridgePlayer.getPlayer(), posChest, lookSpeed, 90);
					Corobot.playerAI.bridgePlayer.getPlayer().rotationPitch += 30;
				}
				
				ticksPathing++;
				if (ticksPathing >= ticksPathingMax) {
					return EnumBehaviorState.FAILURE;
				}
			}
			//Corobot.dbg("state: " + state);
		/*} else {
			System.out.println("cant find chest");
			//Corobot.getPlayerAI().planGoal.invalidatePlan();
			return EnumBehaviorState.FAILURE;
		}*/
		
		return EnumBehaviorState.RUNNING;
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
	public void reset() {
		super.reset();
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
		ticksPathing = 0;
	}

}
