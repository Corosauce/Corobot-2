package corobot.ai;

import javax.vecmath.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.corosus.ai.Blackboard;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.minigoap.PlanGoal;
import com.corosus.ai.minigoap.PlanPiece;
import com.corosus.ai.minigoap.PlanRegistry;
import com.corosus.ai.path.Path;
import com.corosus.entity.IEntity;
import com.corosus.world.IWorld;

import corobot.Corobot;
import corobot.ai.behaviors.OrdersTasks;
import corobot.ai.memory.helper.HelperInventory;
import corobot.ai.memory.helper.HelperItemUsing;
import corobot.ai.minigoap.plans.PlanCraftRecipe;
import corobot.ai.minigoap.plans.PlanHarvestCrop;
import corobot.ai.minigoap.plans.PlanMaintainHouse;
import corobot.ai.minigoap.plans.PlanGetResource;
import corobot.ai.minigoap.plans.PlanPlantCrop;
import corobot.ai.minigoap.plans.PlanTillGrass;
import corobot.ai.profile.ProfilePlayer;
import corobot.bridge.PlayerBridge;
import corobot.bridge.WorldBridge;
import corobot.util.InventoryInfo;
import corobot.util.UtilFurnace;
import corobot.util.UtilPlayer;
import corobot.util.UtilRecipe;

public class PlayerAI implements IEntity {

	public AIBTAgentImpl agent;
	public PlayerBridge bridgePlayer;
	public WorldBridge bridgeWorld;
	public InventoryInfo invInfo;
	
	public boolean needInit = true;
	
	public static BehaviorNode lastNodeRun;
	
	public boolean useGOAP = false;
	
	public PlayerAI() {
	}
	
	public static void setNodeDBG(BehaviorNode node) {
		if (node != lastNodeRun) {
			System.out.println("running node: " + node);
		}
		lastNodeRun = node;
	}
	
	public void initGOAP() {

		
		//need a way to mark 'has farmland'
		
		Blackboard bb = Corobot.getPlayerAI().agent.getBlackboard(); 
		
		PlanRegistry.addPlanPiece(new PlanHarvestCrop("harvestWheat", bb, new ItemStack(Items.wheat), new ItemStack(Blocks.wheat)));
		
		PlanRegistry.addPlanPiece(new PlanPlantCrop("plantWheat", bb, Blocks.wheat, new ItemStack(Items.wheat_seeds)));
		
		PlanRegistry.addPlanPiece(new PlanTillGrass("tillGrass", bb, new ItemStack(Blocks.farmland), new ItemStack(Items.wooden_hoe)));
		
		PlanRegistry.addPlanPiece(new PlanGetResource("chopTallgrass", bb, new ItemStack(Items.wheat_seeds), Blocks.tallgrass, 0, null));
		
		PlanRegistry.addPlanPiece(new PlanMaintainHouse("maintain house", bb));
		
		/*PlanRegistry.addPlanPiece(new PlanCraftRecipeManual("craftWoodPickaxe", new ItemStack(Items.wooden_pickaxe), new ItemStack(Items.stick, 2), new ItemStack(Blocks.planks, 3)));
		PlanRegistry.addPlanPiece(new PlanCraftRecipeManual("craftWoodHoe", new ItemStack(Items.wooden_hoe), new ItemStack(Items.stick, 2), new ItemStack(Blocks.planks, 2)));
		PlanRegistry.addPlanPiece(new PlanCraftRecipeManual("craftStonePickaxe", new ItemStack(Items.stone_pickaxe), new ItemStack(Items.stick, 2), new ItemStack(Blocks.cobblestone, 3)));
		
		PlanRegistry.addPlanPiece(new PlanCraftRecipeManual("craftWoodSticks", new ItemStack(Items.stick, 2), new ItemStack(Blocks.planks)));*/
		
		//NEEDS TO KNOW WHAT CAN MINE THEM!
		//NEEDS TO KNOW THERE ARE 'OR' statements on what pickaxe can be used, like, minimum required
		PlanRegistry.addPlanPiece(new PlanGetResource("mineLog0", bb, Blocks.log, 0, null));
		PlanRegistry.addPlanPiece(new PlanGetResource("mineLog1", bb, Blocks.log, 1, null));
		PlanRegistry.addPlanPiece(new PlanGetResource("mineLog2", bb, Blocks.log, 2, null));
		PlanRegistry.addPlanPiece(new PlanGetResource("mineLog3", bb, Blocks.log, 3, null));
		PlanRegistry.addPlanPiece(new PlanGetResource("mineCobble", bb, Blocks.cobblestone, 0, new ItemStack(Items.wooden_pickaxe)));
		PlanRegistry.addPlanPiece(new PlanGetResource("mineStone", bb, new ItemStack(Blocks.cobblestone), Blocks.stone, 0, new ItemStack(Items.wooden_pickaxe)));
		PlanRegistry.addPlanPiece(new PlanGetResource("mineCoal", bb, new ItemStack(Items.coal), Blocks.coal_ore, 0, new ItemStack(Items.stone_pickaxe)));
		PlanRegistry.addPlanPiece(new PlanGetResource("mineIron", bb, /*new ItemStack(Items.iron_ingot), */Blocks.iron_ore, 0, new ItemStack(Items.stone_pickaxe)));
		PlanRegistry.addPlanPiece(new PlanGetResource("mineDiamond", bb, new ItemStack(Items.diamond), Blocks.diamond_ore, 0, new ItemStack(Items.iron_pickaxe)));
		PlanRegistry.addPlanPiece(new PlanGetResource("mineGrass", bb, new ItemStack(Blocks.dirt), Blocks.grass, 0, null));
		PlanRegistry.addPlanPiece(new PlanGetResource("mineDirt", bb, new ItemStack(Blocks.dirt), Blocks.dirt, 0, null));
		//PlanRegistry.addPlanPiece(new PlanMineBlockNewSequence("minePalm", TCBlockRegistry.planks));
		//PlanRegistry.addPlanPiece(new PlanCraftRecipeManual("craftWoodPlanks", new ItemStack(Blocks.planks), new ItemStack(Blocks.log)));
		
		UtilRecipe.addRecipePlans();
		UtilFurnace.addFurnacePlans();
	}
	
	public void init() {
		
		invInfo = new InventoryInfo();
		bridgePlayer = new PlayerBridge(this);
		bridgeWorld = new WorldBridge(this);
		
		needInit = false;
		agent = new AIBTAgentImpl(this);
		agent.setTickRateDelay(1);
		//this is multiplied by tick rate
		agent.getBlackboard().setDelayNewPathfind(40);

		initGOAP();
		
		//this should be relocated
		OrdersTasks tasks = new OrdersTasks(null, agent.getBlackboard());
		agent.getBtTemplate().ordersHandler.setOrders(tasks);
		
		agent.setProfile(new ProfilePlayer(agent));
		agent.init();
		
		agent.getBtTemplate().btSenses.getChildren().clear();
		agent.getBtTemplate().btSenses.add(new SenseEnvironmentPlayer(agent.getBtTemplate().btSenses, agent.getBlackboard()));
		
		//temp - or not?
		Path path = new Path(agent.getBlackboard());
		agent.getBlackboard().setPath(path);
	}
	
	public void tickUpdate() {
		if (needInit) init();
		if (bridgePlayer.getPlayer() == null) return;
		agent.tickUpdate();
		HelperItemUsing.tickUsageUpdate();
		
		BehaviorNode.DEBUG = false;
		
		bridgePlayer.tickUpdate();
		
		if (useGOAP) {
			updateGOAP();
		}
		
		HelperInventory.updateCache(agent.getBlackboard().getWorldMemory(), HelperInventory.selfInventory, Corobot.playerAI.bridgePlayer.getPlayer().inventory);
	}
	
	public void updateGOAP() {

		
		//test stuff
		BlackboardImpl bb = (BlackboardImpl) agent.getBlackboard();
		
		{
		/*List<ItemStack> stacks = new ArrayList<ItemStack>();
		stacks.add(new ItemStack(Items.stone_pickaxe));
		stacks.add(new ItemStack(Items.baked_potato, 20));
		
		InventoryCollection col = new InventoryCollection(stacks, new InventorySourceSelf());*/
		//bb.getPlayerMemory().getProperties().clear();
		//bb.getPlayerMemory().getProperties().add(new ItemEntry(new ItemStack(Items.stone_pickaxe), new InventorySourceSelf()));
		//bb.getPlayerMemory().getProperties().add(new ItemEntry(new ItemStack(Items.baked_potato, 20), new InventorySourceSelf()));
		//bb.getPlayerMemory().getProperties().add(new ItemEntry(new ItemStack(Blocks.log, 20), new InventorySourceSelf()));
		
		//bb.getPlayerMemory().getProperties().add(new MachineLocation(new Vector3f(-45, 69, 231), Blocks.crafting_table));
		//bb.getPlayerMemory().getProperties().add(new ResourceLocation(new Vector3f(-45, 69, 230), Blocks.log));
		
		//bb.getPlayerMemory().getProperties().add(new ResourceLocation(new Vector3f(-45, 69, 230), TCBlockRegistry.planks));
		}
		
		/*PlayerMemoryState precondition = new PlayerMemoryState();
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		stacks.add(new ItemStack(Items.stone_pickaxe));
		stacks.add(new ItemStack(Items.baked_potato, 10));
		
		InventoryCollection col = new InventoryCollection(stacks, new InventorySourceSelf());
		precondition.listInventories.add(col);
		
		precondition.listMachineLocations.add(new MachineLocation(new Vector3f(1, 2, 4), Blocks.crafting_table));*/
		
		
		
		if (Minecraft.getMinecraft().theWorld.getTotalWorldTime() % 40 == 0) {
			//planGoal.invalidatePlan();
		}
		
		/*if (!planGoal.hasPlan() || planGoal.isPlanComplete()) {
			//planGoal.createPlan(PlanRegistry.getPlanPieceByName("harvestWheat"), bb.getWorldMemory());
			//planGoal.createPlan(PlanRegistry.getPlanPieceByName("Wooden Hoe124"), bb.getWorldMemory());
			PlanPiece endGoal = PlanRegistry.getPlanPieceByNamePartial("diamond pickaxe");
			if (endGoal instanceof PlanCraftRecipe) {
				((PlanCraftRecipe) endGoal).setAmountToCraft(64);
			}
			planGoal.createPlan(endGoal, bb.getWorldMemory());
			
			
			//planGoal.createPlan(PlanRegistry.getPlanPieceByName("craftWoodPlanks"), bb.getWorldMemory());
		}*/
		
		//if (true) return;
		
		/*if (planGoal.getListPlanPieces().size() > 0) {
			PlanPiece plan = planGoal.getListPlanPieces().get(planGoal.getCurPlanIndex());
			agent.getBtTemplate().ordersHandler.setOrders(plan);
			if (plan.isTaskComplete()) {
				plan.endTask();
				System.out.println(planGoal.getCurPlanIndex() + " task complete, moving to next: " + (planGoal.getCurPlanIndex() + 1));
				planGoal.setCurPlanIndex(planGoal.getCurPlanIndex()+1);
				//plan = planGoal.getListPlanPieces().get(planGoal.getCurPlanIndex());
			}
		}*/
		
		//boolean result = bb.getPlayerMemory().contains(precondition);
		
		//System.out.println("plan size: " + goal.getListPlanPieces().size());
		//if (planGoal != null) System.out.println(planGoal.getCurPlanIndex() + " - " + planGoal);
	}
	
	public void updateCache() {
		EntityPlayer player = bridgePlayer.getPlayer();
    	UtilPlayer.updateBestWeaponSlot(player, player.inventory, invInfo);
    	
    	updateOptimalItemSlots();
	}
	
	public void updateOptimalItemSlots() {
		UtilPlayer.optimizeOffensiveInventory();
		UtilPlayer.optimizeDefensiveInventory();
	}
	
	@Override
	public IWorld getLevel() {
		return bridgeWorld;
	}

	@Override
	public Vector3f getMvtvec() {
		return agent.getBlackboard().getPath().getCurMoveTo();
	}

	@Override
	public Vector3f getPos() {
		return new Vector3f((float)bridgePlayer.getPlayer().posX, (float)bridgePlayer.getPlayer().boundingBox.minY, (float)bridgePlayer.getPlayer().posZ);
	}

	@Override
	public void stopMoving() {
		agent.getBlackboard().getPath().clearPath();
	}

	@Override
	public void computePath(Vector3f moveTo) {
		//Path path = new Path(agent.getBlackboard());
		agent.getBlackboard().getPath().setPathNodes(bridgePlayer.computePath(moveTo));
	}

	/**
	 * Supposed to be used for when pathless movment is done, but its not at the moment, its rewired to path computing
	 */
	@Override
	public void setMoveTo(Vector3f parMoveTo) {
		//TODO: refine movement decisions in future, using A* for now
		if (agent.getActor().getLevel().getTicksTotal() % (agent.getTickRateDelay() * agent.getBlackboard().getDelayNewPathfind()) == 0) {
			computePath(parMoveTo);
		}
		
	}

	@Override
	public void setPos(Vector3f parVec) {
		bridgePlayer.getPlayer().setPosition(parVec.x, parVec.y, parVec.z);
	}

	@Override
	public boolean getIsDead() {
		return bridgePlayer.getPlayer().isDead;
	}

	@Override
	public float getHealthMax() {
		return bridgePlayer.getPlayer().getMaxHealth();
	}

	@Override
	public float getHealthCur() {
		return bridgePlayer.getPlayer().getHealth();
	}

}
