package corobot.ai.behaviors.misc;

import javax.vecmath.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.corosus.ai.AIBTAgent;
import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.leaf.LeafNode;
import com.corosus.ai.bt.nodes.tree.Sequence;
import com.corosus.entity.IEntity;
import com.corosus.util.VecUtil;
import com.corosus.world.IWorld;

import corobot.Corobot;
import corobot.ai.BlackboardImpl;

public class TaskConstructPath extends LeafNode {

	public boolean startedBuilding = false;
	public Vector3f posStart = null;
	public Vector3f posCur = null;
	public float curDist = 0;
	
	public Sequence sequenceTasks;
	
	public TaskConstructPath(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
		
		sequenceTasks = new Sequence(this, getBlackboard());
		
		TaskMoveToPos moveTo = new TaskMoveToPos(sequenceTasks, getBlackboard());
		
		sequenceTasks.add(moveTo);
	}

	@Override
	public EnumBehaviorState tick() {
		
		BlackboardImpl bb = (BlackboardImpl) getBlackboard();
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		World worldMC = Minecraft.getMinecraft().theWorld;
		Minecraft mc = Minecraft.getMinecraft();
		
		Vector3f posPlayer = player.getPos();
		Vector3f posEnd = bb.getPathConstructEnd();
		
		if (!startedBuilding) {
			startedBuilding = true;
			posStart = new Vector3f(posPlayer);
			posCur = new Vector3f(posPlayer);
		}
		
		if (VecUtil.getDistSqrd(posCur, posEnd) < 1) {
			return EnumBehaviorState.SUCCESS;
		}
		
		Vector3f angle = VecUtil.getAngle(posStart, posEnd);
		
		Vector3f posConstruct = new Vector3f(angle);
		posConstruct.scaleAdd(curDist, posStart);
		
		int x = MathHelper.floor_float(posConstruct.x);
		int y = MathHelper.floor_float(posConstruct.y);
		int z = MathHelper.floor_float(posConstruct.z);
		
		Block posGround = worldMC.getBlock(x, y, z);
		Block posAir1 = worldMC.getBlock(x, y+1, z);
		Block posAir2 = worldMC.getBlock(x, y+2, z);
			
		if (posAir2 != Blocks.air) {
			//need to dig
		} else if (posAir1 != Blocks.air) {
			//need to dig
		} else if (!posGround.getMaterial().isSolid()) {
			//need to place
		} else {
			curDist += 1;
		}
		
		return EnumBehaviorState.RUNNING;
	}
	
	@Override
	public void reset() {
		startedBuilding = false;
		curDist = 0;
		posStart = null;
		posCur = null;
		super.reset();
	}
}
