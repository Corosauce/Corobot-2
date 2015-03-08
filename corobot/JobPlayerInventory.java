package corobot;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.src.c_CoroAIUtil;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Vec3;

import CoroAI.entity.EnumJobState;
import CoroAI.entity.JobManager;

public class JobPlayerInventory extends c_Job_Player {
	
	public int waitTimeout = 20;
	public int curWaitTimeout;
	public EnumJobState packetState; //IDLE = not waiting, W1 = waiting, W2 = timed out
	
	public JobPlayerInventory(JobManager jm) {
		super(jm);
		packetState = EnumJobState.IDLE;
	}
	
	@Override
	public boolean shouldExecute() {
		return missingNeededItem();//ent.getFoodLevel() <= 17;
	}
	
	@Override
	public boolean shouldContinue() {
		return !missingNeededItem();//ent.getFoodLevel() > 17;
	}
	
	public boolean missingNeededItem() {
		if (c_AIP.i.player.inventory.mainInventory[0] == null || c_AIP.i.player.inventory.mainInventory[1] == null || c_AIP.i.player.inventory.mainInventory[2] == null) return true;
		
		if (!(c_AIP.i.player.inventory.mainInventory[0].getItem() instanceof ItemSword)) return true;
		
		if (!(c_AIP.i.player.inventory.mainInventory[1].getItem() instanceof ItemBow)) return true;
		
		if (!(c_AIP.i.player.inventory.mainInventory[2].getItem() instanceof ItemFood)) return true;
		
		return false;
	}
	
	@Override
	public void tick() {
		
		if (curWaitTimeout > 0) {
			curWaitTimeout--;
			if (curWaitTimeout <= 0) {
				requestTimeout();
			}
		}
		
		//Handle any timeout based resets or retries to states here before packet status resets
		if (packetState == EnumJobState.W2) {
			if (state == EnumJobState.W1) {
				//nadda
			}
			packetState = EnumJobState.IDLE;
		}
		
		
		//if (!(state == EnumJobState.IDLE)) { ent.setEntityToAttack(null); }
		
		//if (fakePlayer.foodStats.getFoodLevel() > 17) { swapJob(EnumJob.mFISHERMAN); }
		//if (ent.getFoodLevel() > 17) { ent.swapJob(jm.priJob); }
		
		//ent = c_AIP.i.player;
		
		if (state == EnumJobState.IDLE) {
			c_AIP.i.walkTo(c_AIP.i.player, c_AIP.i.homeX, c_AIP.i.homeY, c_AIP.i.homeZ, c_AIP.i.maxPFRange, 600);
			setJobState(EnumJobState.W1);
		} else if (state == EnumJobState.W1) {
			
			if (c_AIP.i.player.getDistance(c_AIP.i.homeX, c_AIP.i.homeY, c_AIP.i.homeZ) < 3F) {
				//ent.faceCoord((int)(ent.homeX-0.5F), (int)ent.homeY, (int)(ent.homeZ-0.5F), 180, 180);
				if (packetState == EnumJobState.IDLE) {
					if (openChestGUI(c_AIP.i.homeX, c_AIP.i.homeY, c_AIP.i.homeZ)) {
						requestSent();
					}
				} else {
					if (c_AIP.i.mc.currentScreen instanceof GuiChest) {
						requestReset();
						setJobState(EnumJobState.W2);
						
					}
				}
				//c_AIP.i.eat();
				//ent.swapJob(jm.priJob);
			} else if (walkingTimeout <= 0 || c_AIP.i.pathToEntity == null) {
				//this.setPathExToEntity(null);
				c_AIP.i.walkTo(c_AIP.i.player, c_AIP.i.homeX, c_AIP.i.homeY, c_AIP.i.homeZ, c_AIP.i.maxPFRange, 600);
			}
		} else if (state == EnumJobState.W2) {
			if (c_AIP.i.mc.currentScreen instanceof GuiChest) {
				if (packetState == EnumJobState.IDLE) {
					clickTest((GuiChest)c_AIP.i.mc.currentScreen);
				} else {
					
				}
			} else {
				setJobState(EnumJobState.IDLE);
				requestReset();
			}
		}
	}
	
	public void requestSent() {
		packetState = EnumJobState.W1;
		curWaitTimeout = waitTimeout;
	}
	
	public void requestReset() {
		packetState = EnumJobState.IDLE;
	}
	
	public void requestTimeout() {
		packetState = EnumJobState.W2;
	}
	
	public void clickTest(GuiChest gui) {
		requestSent();
		
		int x = 0;
		int y = 0;
		int mouse2StepClick = 0;
		int mouseShiftClick = 1;
		int windowID = c_AIP.i.player.openContainer.windowId; //what is this for exactly???
		
		int slotCount = ((ContainerChest)gui.inventorySlots).getInventory().size();
		int chestSlotCount = ((ContainerChest)gui.inventorySlots).getLowerChestInventory().getSizeInventory();
		int myInvSlotStart = chestSlotCount;
		int hotSlotStart = chestSlotCount;
		
		System.out.println("slot count: " + chestSlotCount);
		
		c_AIP.i.mc.playerController.windowClick(windowID, /*myInvSlotStart+*/27, y, mouseShiftClick, c_AIP.i.mc.thePlayer);
		
		try {
			
			//transfer inv
			for (int i = 0; i < 27; i++) {
				ItemStack is = c_AIP.i.player.inventory.mainInventory[i+9];
				
				if (is != null) {
					System.out.println(is);
					c_AIP.i.mc.playerController.windowClick(windowID, myInvSlotStart+i, y, mouseShiftClick, c_AIP.i.mc.thePlayer);
				}
			}
			
			//transfer hotbar minus 3 slots
			for (int i = 0; i < 9; i++) {
				ItemStack is = c_AIP.i.player.inventory.mainInventory[i];
				
				if (is != null) {
					System.out.println(is);
					c_AIP.i.mc.playerController.windowClick(windowID, myInvSlotStart+27+i, y, mouseShiftClick, c_AIP.i.mc.thePlayer);
				}
			}
			
			int bestSwordSlot = -1;
			int bestSwordDmg = 0;
			
			int bestBowSlot = -1;
			int bestBowSelfDmg = 99999;
			
			int bestFoodSlot = -1;
			int bestFoodHeal = 0;
			
			/*ItemStack is = c_AIP.i.player.inventory.mainInventory[0];
			
			if (is != null && is.getItem() instanceof ItemSword) {
				bestSwordDmg = is.getItem().getDamageVsEntity(c_AIP.i.player);
				bestSwordSlot = -1;
			}*/
			
			//take food and supplies
			for (int i = 0; i < chestSlotCount; i++) {
				ItemStack is = (ItemStack)((ContainerChest)gui.inventorySlots).getLowerChestInventory().getStackInSlot(i);
				
				if (is != null) {
					
					x = i;
					
					Item item = is.getItem();
					
					if (item instanceof ItemSword) {
						if (item.getDamageVsEntity(c_AIP.i.player) > bestSwordDmg) {
							bestSwordDmg = item.getDamageVsEntity(c_AIP.i.player);
							bestSwordSlot = i;
						}
					} else if (item instanceof ItemBow) {
						if (is.getItemDamage() < bestBowSelfDmg) {
							bestBowSelfDmg = is.getItemDamage();
							bestBowSlot = i;
						}
					} else if (item instanceof ItemFood) {
						if (((ItemFood) item).getHealAmount() > bestFoodHeal) {
							bestFoodHeal = ((ItemFood) item).getHealAmount();
							bestFoodSlot = i;
						}
					}
				}
			}
			
			System.out.println(bestSwordSlot);
			
			if (bestSwordSlot != -1) {
				c_AIP.i.mc.playerController.windowClick(windowID, bestSwordSlot, y, mouse2StepClick, c_AIP.i.mc.thePlayer);
				c_AIP.i.mc.playerController.windowClick(windowID, myInvSlotStart+27+0, y, mouse2StepClick, c_AIP.i.mc.thePlayer);
			}
			
			if (bestBowSlot != -1) {
				c_AIP.i.mc.playerController.windowClick(windowID, bestBowSlot, y, mouse2StepClick, c_AIP.i.mc.thePlayer);
				c_AIP.i.mc.playerController.windowClick(windowID, myInvSlotStart+27+1, y, mouse2StepClick, c_AIP.i.mc.thePlayer);
			}
			
			if (bestFoodSlot != -1) {
				c_AIP.i.mc.playerController.windowClick(windowID, bestFoodSlot, y, mouse2StepClick, c_AIP.i.mc.thePlayer);
				c_AIP.i.mc.playerController.windowClick(windowID, myInvSlotStart+27+2, y, mouse2StepClick, c_AIP.i.mc.thePlayer);
			}
			
			for (int i = 0; i < chestSlotCount; i++) {
				ItemStack is = (ItemStack)((ContainerChest)gui.inventorySlots).getLowerChestInventory().getStackInSlot(i);
				
				if (is != null) {
					
					x = i;
					
					//Item item = is.getItem();
					
					if (is.itemID == Item.arrow.shiftedIndex) {
						c_AIP.i.mc.playerController.windowClick(windowID, i, y, mouseShiftClick, c_AIP.i.mc.thePlayer);
					}
				}
			}
			
			c_AIP.i.mc.displayGuiScreen(new GuiChat(""));
		} catch (Exception ex) {
			
		}
		
		
	}
	
	public boolean openChestGUI(int x, int y, int z) {
		if (isChest(c_AIP.i.worldObj.getBlockId(x, y-1, z))) {
			y--;
		}
		boolean transferred = false;
		if (isChest(c_AIP.i.worldObj.getBlockId(x, y, z))) {
			
			//c_AIP.i.mc.playerController.clickBlock(x, y, z, 0);
			c_AIP.i.mc.playerController.onPlayerRightClick(c_AIP.i.player, c_AIP.i.worldObj, c_AIP.i.player.getCurrentEquippedItem(), x, y, z, 0, Vec3.createVectorHelper(x, y, z));
			
			return true;
			
			//TileEntityChest chest = (TileEntityChest)worldObj.getBlockTileEntity(x, y, z);
			//if (chest != null) {
				
				//openHomeChest();
				
				//transferItems(chest, player.inventory, id, 1, food);
			//}
		}
		
		return false;
	}
	
	@Override
	public void setJobItems() {
		c_AIP.i.wantedItems.add(Item.arrow.shiftedIndex);
		c_AIP.i.wantedItems.add(Item.rottenFlesh.shiftedIndex);
	}
	
	@Override
	public void transferItems(IInventory invFrom, IInventory invTo, int id, int transferCount, boolean foodOverride) {
		
	}
	
	private void transferItems(TileEntityChest chest, InventoryPlayer inventory, int id, int j, boolean food) {
			System.out.println("Corobot transfer items");
		
	}

	public boolean isChest(int id) {
		return c_CoroAIUtil.isChest(id);
	}
	
}
