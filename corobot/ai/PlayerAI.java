package corobot.ai;

import javax.vecmath.Vector3f;

import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.path.Path;
import com.corosus.entity.IEntity;
import com.corosus.world.IWorld;

import corobot.Corobot;
import corobot.ai.behaviors.AvoidClosestThreat;
import corobot.ai.behaviors.JumpForBoredom;
import corobot.ai.behaviors.OrdersTasks;
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
		
		//temp
		if (getLevel().getTicksTotal() % 20 == 0) {
			//if (agent.getBlackboard().getPath().isComplete()) {
				//System.out.println("try path");
				
				//computePath(new Vector3f(0, 64, 0));
			/*if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
				//computePath(new Vector3f(-37, 65, 215));
				computePath(new Vector3f(12, 69, 205));
			}*/
				//System.out.println(agent.getBlackboard().getPath().listPathnodes.size());
			//}
		}
		bridgePlayer.tickUpdate();
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
