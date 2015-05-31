package corobot.ai.behaviors.yd;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.tree.Sequence;

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
		
		//add(new RightClickBlock(this, getBlackboard(), posKitArcher));
		//add(new JumpForBoredom(this, getBlackboard()));
		//add(new RightClickBlock(this, getBlackboard(), posTeamBlueJoin));
		//maybe add a 'sense teleport' behavior here? if it jumps right to RightClickBlock, its pathing will fail eventually, causing a FAILURE return
		
		//replace with basic moveto behavior
		add(new PlanMoveToPos("moveTo", getBlackboard(), posOresMiddle));
		PlanSearchMineBlock plan = new PlanSearchMineBlock("findDiamonds", getBlackboard(), new ItemStack(Items.diamond), Blocks.diamond_ore, 0, new ItemStack(Items.diamond_pickaxe));
		plan.countNeeded = 5;
		add(plan);
		//add(new RightClickBlock(this, getBlackboard(), posTeamBlueChest));
		List<ItemStack> listStacks = new ArrayList<ItemStack>();
		listStacks.add(new ItemStack(Items.diamond, 5));
		//add(new PlanTranferToAndFromChest("dropOffToChest", getBlackboard(), posTeamBlueChest, listStacks, listStacks));
		add(new PlanTranferToAndFromChest("dropOffToChest", getBlackboard(), posTeamBlueChest, null, listStacks));
	}

	@Override
	public EnumBehaviorState tick() {
		return super.tick();
	}
	
}
