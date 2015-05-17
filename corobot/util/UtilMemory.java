package corobot.util;

import java.util.List;

import com.corosus.ai.AIBTAgent;
import com.corosus.ai.Blackboard;
import com.corosus.ai.minigoap.IWorldStateProperty;
import com.corosus.entity.IEntity;
import com.corosus.util.VecUtil;
import com.corosus.world.IWorld;

import net.minecraft.block.Block;
import corobot.Corobot;
import corobot.ai.memory.pieces.BlockLocation;
import corobot.ai.memory.pieces.ResourceLocation;

public class UtilMemory {

	public static BlockLocation getClosestBlock(Block block) {
		BlockLocation closestLocation = null;
		double closestDist = 99999;
		
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		
		Blackboard bb = agent.getBlackboard();
		
		List<IWorldStateProperty> props = bb.getWorldMemory().getProperties();
		
		for (IWorldStateProperty prop : props) {
			if (prop instanceof BlockLocation) {
				
				if (((BlockLocation)prop).getBlock() == block) {
					double dist = VecUtil.getDistSqrd(player.getPos(), ((BlockLocation) prop).getPos());
					if (dist < closestDist) {
						closestDist = dist;
						closestLocation = (BlockLocation) prop;
					}
					
					//break;
				}
			}
		}
		
		return closestLocation;
	}
	
}
