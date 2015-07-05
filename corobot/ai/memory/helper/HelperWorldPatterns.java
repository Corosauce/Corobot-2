package corobot.ai.memory.helper;

import java.util.HashMap;

import javax.vecmath.Vector3f;

import com.corosus.ai.AIBTAgent;
import com.corosus.entity.IEntity;
import com.corosus.world.IWorld;

import corobot.Corobot;
import corobot.ai.BlackboardImpl;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class HelperWorldPatterns {

	/* ore profiling:
	 * - location
	 * -- x y z, mostly y
	 * - biome if relevant
	 * - behaviores required in getting it (dont mine top of trees we cant path to, or pillar up to it)
	 * - tool type required to use ! (pickaxe, shovel)
	 * -- tool LEVEL required to use! (wooden, iron)
	 */
	
	public static HashMap<Block, OrePattern> lookupBlockToPattern = new HashMap<Block, OrePattern>();
	
	static {
		lookupBlockToPattern.put(Blocks.coal_ore, new OrePattern(5, 52));
		lookupBlockToPattern.put(Blocks.iron_ore, new OrePattern(5, 54));
		lookupBlockToPattern.put(Blocks.gold_ore, new OrePattern(5, 29));
		lookupBlockToPattern.put(Blocks.lapis_ore, new OrePattern(14, 16));
		lookupBlockToPattern.put(Blocks.diamond_ore, new OrePattern(5, 12));
		lookupBlockToPattern.put(Blocks.redstone_ore, new OrePattern(5, 12));
		lookupBlockToPattern.put(Blocks.lit_redstone_ore, new OrePattern(5, 12));
		lookupBlockToPattern.put(Blocks.emerald_ore, new OrePattern(5, 29));
		lookupBlockToPattern.put(Blocks.grass, new OrePattern(true));
		lookupBlockToPattern.put(Blocks.dirt, new OrePattern(true));
		lookupBlockToPattern.put(Blocks.sand, new OrePattern(true));
		lookupBlockToPattern.put(Blocks.log, new OrePattern(true));
		lookupBlockToPattern.put(Blocks.log2, new OrePattern(true));
		lookupBlockToPattern.put(Blocks.stone, new OrePattern(true));
		lookupBlockToPattern.put(Blocks.cobblestone, new OrePattern(true));
	}
	
	//TODO: this will probably be an actual task, remove if so
	public static void setNewPathConstruct(Block ore) {

		AIBTAgent agent = Corobot.getPlayerAI().agent;
		BlackboardImpl bb = (BlackboardImpl) agent.getBlackboard();
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		World worldMC = Minecraft.getMinecraft().theWorld;
		Minecraft mc = Minecraft.getMinecraft();
		
		Vector3f posPlayer = player.getPos();
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
	}
}
