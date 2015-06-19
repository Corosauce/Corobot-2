package corobot.util;

import java.util.List;

import javax.vecmath.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.corosus.ai.AIBTAgent;
import com.corosus.ai.Blackboard;
import com.corosus.ai.minigoap.IWorldStateProperty;
import com.corosus.entity.IEntity;
import com.corosus.util.VecUtil;
import com.corosus.world.IWorld;

import corobot.Corobot;
import corobot.ai.memory.helper.HelperBlock;
import corobot.ai.memory.helper.HelperHouse;
import corobot.ai.memory.pieces.BlockLocation;
import corobot.ai.memory.pieces.MachineLocation;
import corobot.ai.memory.pieces.ResourceLocation;

public class UtilMemory {

	public static BlockLocation getClosestBlockFromMemory(Block block, int meta) {
		BlockLocation closestLocation = null;
		double closestDist = 99999;
		
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		
		Blackboard bb = agent.getBlackboard();
		
		List<IWorldStateProperty> props = bb.getWorldMemory().getProperties();
		
		for (IWorldStateProperty prop : props) {
			if (prop instanceof BlockLocation) {
				
				if (((BlockLocation)prop).getBlock() == block && (((BlockLocation)prop).getMeta() == meta || meta == -1)) {
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
	
	public static Vector3f getClosestBlockFromArea(Block blockToFind, int metaToFind, Vector3f pos) {
		Vector3f closestLocation = null;
		double closestDist = 99999;
		
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		World worldMC = Minecraft.getMinecraft().theWorld;
		IEntity player = Corobot.getPlayerAI();
		
		Blackboard bb = agent.getBlackboard();
		
		int range = 10;
		int rangeY = 3;
		
		for (int x = -range; x < range; x++) {
			for (int z = -range; z < range; z++) {
				for (int y = -rangeY; y < rangeY; y++) {
					int xx = MathHelper.floor_double(pos.x)+x;
					int yy = MathHelper.floor_double(pos.y)+y;
					int zz = MathHelper.floor_double(pos.z)+z;
					Block block = worldMC.getBlock(xx, yy, zz);
					int meta = worldMC.getBlockMetadata(zz, yy, zz);
					
					Vector3f vec = new Vector3f(xx, yy, zz);
					
					if (!HelperHouse.shouldMine(vec)) {
						continue;
					}
					
					if (!UtilEnt.canSeeCoord(player.getPos(), vec)) {
						continue;
					}
					
					if (block == blockToFind && (metaToFind == -1 || meta == metaToFind)) {
						double dist = VecUtil.getDistSqrd(pos, vec);
						if (dist < closestDist) {
							closestDist = dist;
							closestLocation = vec;
						}
					}
				}
			}
		}
		
		return closestLocation;
		
	}
	
}
