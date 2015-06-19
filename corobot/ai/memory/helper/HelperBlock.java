package corobot.ai.memory.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import com.corosus.ai.minigoap.IWorldState;
import com.corosus.ai.minigoap.IWorldStateProperty;

import corobot.Corobot;
import corobot.ai.memory.pieces.BlockLocation;

public class HelperBlock {

	public static HashMap<Integer, IWorldStateProperty> lookupBlocks = new HashMap<Integer, IWorldStateProperty>();
	
	public static List<Block> listResources = new ArrayList<Block>();
	
	public static List<Block> listMachines = new ArrayList<Block>();
	
	//because theyre everywhere and we dont want to store the whole world in memory
	public static List<Block> listResourcesToNotRemember = new ArrayList<Block>(); 
	
	static {
		listResources.add(Blocks.log);
		listResources.add(Blocks.log2);
		
		listResources.add(Blocks.cobblestone);
		listResources.add(Blocks.stone);
		
		listResources.add(Blocks.coal_ore);
		listResources.add(Blocks.iron_ore);
		listResources.add(Blocks.redstone_ore);
		listResources.add(Blocks.lapis_ore);
		listResources.add(Blocks.gold_ore);
		listResources.add(Blocks.emerald_ore);
		listResources.add(Blocks.diamond_ore);
		listResources.add(Blocks.quartz_ore);
		
		listMachines.add(Blocks.crafting_table);
		listMachines.add(Blocks.furnace);
		//TODO: NEED TO MERGE THIS WITH FURNACE
		listMachines.add(Blocks.lit_furnace);
		listMachines.add(Blocks.enchanting_table);
		
		listResourcesToNotRemember.add(Blocks.dirt);
		listResourcesToNotRemember.add(Blocks.grass);
		listResourcesToNotRemember.add(Blocks.sand);
	}
	
	public static int makeHash(int p_75830_0_, int p_75830_1_, int p_75830_2_)
    {
        return p_75830_1_ & 255 | (p_75830_0_ & 32767) << 8 | (p_75830_2_ & 32767) << 24 | (p_75830_0_ < 0 ? Integer.MIN_VALUE : 0) | (p_75830_2_ < 0 ? 32768 : 0);
    }
	
	public static void addEntry(IWorldState memory, int hash, IWorldStateProperty prop) {
		memory.getProperties().add(prop);
		lookupBlocks.put(hash, prop);
	}
	
	public static void removeEntry(IWorldState memory, int hash, IWorldStateProperty prop) {
		memory.getProperties().remove(prop);
		lookupBlocks.remove(hash);
	}
	
	public static void removeEntry(IWorldState memory, IWorldStateProperty prop) {
		if (prop == null) {
			Corobot.dbg("Warning! prop is null!");
			return;
		}
		if (prop instanceof BlockLocation) {
			int hash = makeHash((int)((BlockLocation) prop).getPos().x, (int)((BlockLocation) prop).getPos().y, (int)((BlockLocation) prop).getPos().z);
			removeEntry(memory, hash, prop);
		} else {
			Corobot.dbg("CRITICAL! Tried to use HelperBlock.removeEntry for non WorldLocation property");
		}
	}
	
}
