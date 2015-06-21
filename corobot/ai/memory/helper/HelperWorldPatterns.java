package corobot.ai.memory.helper;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

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
	}
}
