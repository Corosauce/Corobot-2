package corobot.ai.behaviors.misc;

import java.util.Random;

import javax.vecmath.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.corosus.ai.AIBTAgent;
import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.tree.SelectorBoolean;
import com.corosus.ai.bt.nodes.tree.Sequence;
import com.corosus.entity.IEntity;
import com.corosus.util.MutableBoolean;
import com.corosus.util.VecUtil;
import com.corosus.world.IWorld;

import corobot.Corobot;
import corobot.ai.BlackboardImpl;
import corobot.ai.behaviors.resources.TaskMineBlock;
import corobot.ai.memory.helper.HelperWorldPatterns;
import corobot.ai.memory.helper.OrePattern;
import corobot.ai.memory.pieces.BlockLocation;

public class TaskGetResourceSeekLocation extends Sequence {

	public boolean startedBuilding = false;
	public Vector3f posStart = null;
	public Vector3f posCur = null;
	public float curDist = 0;
	
	//public Sequence sequenceTasks;
	public MutableBoolean boolShouldMine = new MutableBoolean();
	public MutableBoolean boolShouldWanderSurface = new MutableBoolean();
	
	public TaskGetResourceSeekLocation(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
		
		//sequenceTasks = new Sequence(this, getBlackboard());
		
		IdleWander wander = new IdleWander(this, getBlackboard());
		TaskMoveToPos moveTo = new TaskMoveToPos(this, getBlackboard());
		
		SelectorBoolean selBoolWander = new SelectorBoolean(this, getBlackboard(), boolShouldWanderSurface);
		
		SelectorBoolean selBoolMine = new SelectorBoolean(this, getBlackboard(), boolShouldMine);
		selBoolMine.add(new TaskPlaceBlock(selBoolMine, getBlackboard()));
		selBoolMine.add(new TaskMineBlock(selBoolMine, getBlackboard()));

		Sequence seq = new Sequence(this, getBlackboard());
		seq.add(moveTo);
		seq.add(selBoolMine);
		
		selBoolWander.add(seq);
		selBoolWander.add(wander);
		
		add(selBoolWander);
	}

	@Override
	public EnumBehaviorState tick() {
		
		if (getActiveBehaviorIndex() != -1) {
			if (getState() == EnumBehaviorState.RUNNING) {
				EnumBehaviorState result = super.tick();
				//to prevent it resetting everything once 1 dig happens, seems like a sketchy fix, might need better solution later
				if (result == EnumBehaviorState.SUCCESS) {
					return EnumBehaviorState.RUNNING;
				} else {
					return result;
				}
			}
		}
		
		BlackboardImpl bb = (BlackboardImpl) getBlackboard();
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		World worldMC = Minecraft.getMinecraft().theWorld;
		Minecraft mc = Minecraft.getMinecraft();
		
		Vector3f posPlayer = new Vector3f(player.getPos());
		posPlayer.y--;

		//TODO: surface block support, atm he goes to 128
		//mostly implemented, reroutes to wandering
		
		//MORE TEMP
		bb.setPathConstructEnd(new Vector3f(posPlayer));
		bb.getPathConstructEnd().add(new Vector3f(50, 0, 0));
		OrePattern orePattern = HelperWorldPatterns.lookupBlockToPattern.get(bb.getBlockToMine());
		if (orePattern != null) {
			
		} else {
			Corobot.dbg("WARNING: missing ore pattern for " + bb.getBlockToMine());
			orePattern = HelperWorldPatterns.lookupBlockToPattern.get(Blocks.redstone_ore);
		}
		
		if (orePattern.isOnSurface()) {
			boolShouldWanderSurface.set(true);
			return super.tick();
		} else {
			boolShouldWanderSurface.set(false);
			bb.getPathConstructEnd().y = orePattern.getYMiddle();
		}
		
		return EnumBehaviorState.SUCCESS;
	}
	
	public boolean canMine(Block block) {
		return block != Blocks.air && block.getMaterial() != Material.water;
	}
	
	@Override
	public void reset() {
		System.out.println("path construct reset");
		startedBuilding = false;
		curDist = 0;
		posStart = null;
		posCur = null;
		super.reset();
	}
}
