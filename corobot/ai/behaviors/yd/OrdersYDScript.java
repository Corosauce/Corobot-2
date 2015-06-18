package corobot.ai.behaviors.yd;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Vector3f;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.tree.Sequence;
import com.corosus.util.VecUtil;

import corobot.Corobot;
import corobot.ai.PlayerAI;
import corobot.ai.behaviors.misc.JumpForBoredom;
import corobot.ai.behaviors.misc.TaskMoveToPos;
import corobot.ai.behaviors.misc.RightClickBlock;

public class OrdersYDScript extends Sequence {

	public static Vector3f posLobbySpawn = new Vector3f(-98, 65, -1);
	
	public static Vector3f posKitArcher = new Vector3f(-91, 66, -4);
	public static Vector3f posKitKnight = new Vector3f(-91, 66, 1);
	public static Vector3f posKitGymnast = new Vector3f(-87, 66, -4);
	public static Vector3f posKitDefenseman = new Vector3f(-87, 66, 1);
	
	public static Vector3f posTeamBlueJoin = new Vector3f(-72, 68, -3);
	public static Vector3f posTeamRedJoin = new Vector3f(-72, 68, 0);
	
	public static Vector3f posTeamBlueChest = new Vector3f(-17, 67, -3);
	public static Vector3f posTeamRedChest = new Vector3f(12, 67, -3);
	
	public static Vector3f posOresMiddle = new Vector3f(-4, 79, -2);
	
	//max distance a player can get from the lobby spawn while still in the lobby
	public double lobbyMaxDist = 32;
	public boolean lobbyMode = true;
	
	
	public OrdersYDScript(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
		
		//use in idle state
		//so ai will auto attack mobs, only does any of this sequence below if no hostile mobs around
		
		//detect if within 5 blocks of lobby spawn (repeatable)
		//- if true, do lobby sequence, then auto switch to game sequence
		
		//lobby sequence:
		//- buy items/kit
		//- join team (red or blue? choose random?)
		//- wait 10(?) seconds
		
		//game sequence:
		//- go to ore area
		//- scan for diamond blocks
		//- mine until 5 diamonds in inventory
		//- go to chest
		//- place diamonds in chest
		
		//what about pvp? scan for differently colored names?
		
		
		//maybe add a 'sense teleport' behavior here? if it jumps right to RightClickBlock, its pathing will fail eventually, causing a FAILURE return
		
		//replace with basic moveto behavior
		
		initLobbyTree();
		
	}

	@Override
	public EnumBehaviorState tick() {
		
		PlayerAI playerAI = (PlayerAI) this.getBlackboard().getAgent().getActor();
		EntityPlayer player = playerAI.bridgePlayer.getPlayer();
		
		double distFromLobby = VecUtil.getDistSqrd(posLobbySpawn, playerAI.getPos());
		boolean lobbyModeDetected = false;
		
		if (distFromLobby < lobbyMaxDist) {
			lobbyModeDetected = true;
		}
		
		if (lobbyMode) {
			if (!lobbyModeDetected) {
				lobbyMode = false;
				initArenaTree();
			}
		} else {
			if (lobbyModeDetected) {
				lobbyMode = true;
				initLobbyTree();
			}
		}
		
		return super.tick();
	}
	
	public void initLobbyTree() {
		Corobot.dbg("switched to lobby mode");
		getChildren().clear();
		resetActiveBehavior();
		
		add(new RightClickBlock(this, getBlackboard(), posKitKnight));
		Random rand = new Random();
		if (rand.nextBoolean()) {
			add(new RightClickBlock(this, getBlackboard(), posTeamBlueJoin));
		} else {
			add(new RightClickBlock(this, getBlackboard(), posTeamRedJoin));
		}
		add(new JumpForBoredom(this, getBlackboard(), 20*20));
	}
	
	public void initArenaTree() {
		Corobot.dbg("switched to arena mode");
		getChildren().clear();
		resetActiveBehavior();
		
		add(new TaskMoveToPos(this, getBlackboard(), posOresMiddle));
		PlanSearchMineBlock plan = new PlanSearchMineBlock("findDiamonds", getBlackboard(), new ItemStack(Items.diamond), Blocks.diamond_ore, 0, new ItemStack(Items.diamond_pickaxe));
		plan.countNeeded = 5;
		add(plan);
		List<ItemStack> listStacks = new ArrayList<ItemStack>();
		listStacks.add(new ItemStack(Items.diamond, 5));
		add(new PlanTranferToAndFromChest("dropOffToChest", getBlackboard(), posTeamBlueChest, null, listStacks));
	}
	
}
