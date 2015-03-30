package corobot.ai;

import javax.vecmath.Vector3f;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.minigoap.PlanGoal;
import com.corosus.ai.minigoap.PlanRegistry;
import com.corosus.ai.path.Path;
import com.corosus.entity.IEntity;
import com.corosus.world.IWorld;

import corobot.ai.behaviors.OrdersTasks;
import corobot.ai.memory.pieces.ItemEntry;
import corobot.ai.memory.pieces.MachineLocation;
import corobot.ai.memory.pieces.ResourceLocation;
import corobot.ai.memory.pieces.inventory.InventorySourceSelf;
import corobot.ai.minigoap.plans.PlanCraftRecipeManual;
import corobot.ai.minigoap.plans.PlanHarvestCrop;
import corobot.ai.minigoap.plans.PlanMineBlock;
import corobot.ai.minigoap.plans.PlanPlantCrop;
import corobot.ai.minigoap.plans.PlanTillGrass;
import corobot.ai.profile.ProfilePlayer;
import corobot.bridge.PlayerBridge;
import corobot.bridge.WorldBridge;
import corobot.util.InventoryInfo;
import corobot.util.UtilPlayer;

public class PlayerAI implements IEntity {

	public AIBTAgentImpl agent;
	public PlayerBridge bridgePlayer;
	public WorldBridge bridgeWorld;
	public InventoryInfo invInfo;
	
	public boolean needInit = true;
	
	public static BehaviorNode lastNodeRun;
	
	public PlayerAI() {
		
		//need a way to mark 'has farmland'
		
		PlanRegistry.addPlanPiece(new PlanHarvestCrop("harvestWheat", new ItemStack(Items.wheat), new ItemStack(Blocks.wheat)));
		
		PlanRegistry.addPlanPiece(new PlanPlantCrop("plantWheat", Blocks.wheat, new ItemStack(Items.wheat_seeds)));
		
		PlanRegistry.addPlanPiece(new PlanTillGrass("tillGrass", new ItemStack(Blocks.farmland), new ItemStack(Items.wooden_hoe)));
		
		PlanRegistry.addPlanPiece(new PlanMineBlock("mineCoal", Blocks.coal_ore));
		PlanRegistry.addPlanPiece(new PlanMineBlock("mineIron", Blocks.iron_ore));
		PlanRegistry.addPlanPiece(new PlanMineBlock("chopTallgrass", new ItemStack(Items.wheat_seeds), Blocks.tallgrass));
		
		PlanRegistry.addPlanPiece(new PlanCraftRecipeManual("craftWoodPickaxe", new ItemStack(Items.wooden_pickaxe), new ItemStack(Items.stick, 2), new ItemStack(Blocks.planks, 3)));
		PlanRegistry.addPlanPiece(new PlanCraftRecipeManual("craftWoodHoe", new ItemStack(Items.wooden_hoe), new ItemStack(Items.stick, 2), new ItemStack(Blocks.planks, 2)));
		PlanRegistry.addPlanPiece(new PlanCraftRecipeManual("craftStonePickaxe", new ItemStack(Items.stone_pickaxe), new ItemStack(Items.stick, 2), new ItemStack(Blocks.cobblestone, 3)));
		
		PlanRegistry.addPlanPiece(new PlanCraftRecipeManual("craftWoodSticks", new ItemStack(Items.stick, 2), new ItemStack(Blocks.planks)));
		
		PlanRegistry.addPlanPiece(new PlanMineBlock("mineLog", Blocks.log));
		PlanRegistry.addPlanPiece(new PlanCraftRecipeManual("craftWoodPlanks", new ItemStack(Blocks.planks), new ItemStack(Blocks.log)));
	}
	
	public static void setNodeDBG(BehaviorNode node) {
		if (node != lastNodeRun) {
			System.out.println("running node: " + node);
		}
		lastNodeRun = node;
	}
	
	public void init() {
		
		invInfo = new InventoryInfo();
		bridgePlayer = new PlayerBridge(this);
		bridgeWorld = new WorldBridge(this);
		
		needInit = false;
		agent = new AIBTAgentImpl(this);
		agent.setTickRate(1);
		
		OrdersTasks tasks = new OrdersTasks(null);
		agent.getBtTemplate().ordersHandler.setOrders(tasks);
		
		agent.setProfile(new ProfilePlayer(agent));
		agent.init();
		
		agent.getBtTemplate().btSenses.getChildren().clear();
		agent.getBtTemplate().btSenses.add(new SenseEnvironmentPlayer(agent.getBtTemplate().btSenses, agent));
		
		//temp - or not?
		Path path = new Path(agent.getBlackboard());
		agent.getBlackboard().setPath(path);
	}
	
	public void tickUpdate() {
		if (needInit) init();
		if (bridgePlayer.getPlayer() == null) return;
		agent.tickUpdate();
		
		BehaviorNode.DEBUG = true;
		
		bridgePlayer.tickUpdate();
		
		//test stuff
		BlackboardImpl bb = (BlackboardImpl) agent.getBlackboard();
		
		{
		/*List<ItemStack> stacks = new ArrayList<ItemStack>();
		stacks.add(new ItemStack(Items.stone_pickaxe));
		stacks.add(new ItemStack(Items.baked_potato, 20));
		
		InventoryCollection col = new InventoryCollection(stacks, new InventorySourceSelf());*/
		bb.getPlayerMemory().getProperties().clear();
		bb.getPlayerMemory().getProperties().add(new ItemEntry(new ItemStack(Items.stone_pickaxe), new InventorySourceSelf()));
		bb.getPlayerMemory().getProperties().add(new ItemEntry(new ItemStack(Items.baked_potato, 20), new InventorySourceSelf()));
		//bb.getPlayerMemory().getProperties().add(new ItemEntry(new ItemStack(Blocks.log, 20), new InventorySourceSelf()));
		
		bb.getPlayerMemory().getProperties().add(new MachineLocation(new Vector3f(1, 2, 3), Blocks.crafting_table));
		bb.getPlayerMemory().getProperties().add(new ResourceLocation(new Vector3f(1, 2, 3), Blocks.log));
		}
		
		/*PlayerMemoryState precondition = new PlayerMemoryState();
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		stacks.add(new ItemStack(Items.stone_pickaxe));
		stacks.add(new ItemStack(Items.baked_potato, 10));
		
		InventoryCollection col = new InventoryCollection(stacks, new InventorySourceSelf());
		precondition.listInventories.add(col);
		
		precondition.listMachineLocations.add(new MachineLocation(new Vector3f(1, 2, 4), Blocks.crafting_table));*/
		
		PlanGoal goal = new PlanGoal();
		goal.createPlan(PlanRegistry.getPlanPieceByName("harvestWheat"), bb.getWorldMemory());
		
		
		//boolean result = bb.getPlayerMemory().contains(precondition);
		
		//System.out.println("plan size: " + goal.getListPlanPieces().size());
		System.out.println(goal);
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

	@Override
	public void setMoveTo(Vector3f parMoveTo) {
		// TODO Auto-generated method stub
		//this is called when a pathless movement is used
		computePath(parMoveTo);
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
