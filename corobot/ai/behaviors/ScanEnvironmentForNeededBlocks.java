package corobot.ai.behaviors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.vecmath.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.corosus.ai.AIBTAgent;
import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.leaf.LeafNodeBB;
import com.corosus.ai.minigoap.IWorldStateProperty;
import com.corosus.entity.IEntity;
import com.corosus.world.IWorld;

import corobot.Corobot;
import corobot.ai.memory.pieces.MachineLocation;
import corobot.ai.memory.pieces.ResourceLocation;

public class ScanEnvironmentForNeededBlocks extends LeafNodeBB {

	public HashMap<Integer, IWorldStateProperty> lookupBlocks = new HashMap<Integer, IWorldStateProperty>();
	
	public List<Block> listResources = new ArrayList<Block>();
	
	public List<Block> listMachines = new ArrayList<Block>();
	
	public ScanEnvironmentForNeededBlocks(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
		
		listResources.add(Blocks.log);
		listResources.add(Blocks.log2);
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
	}
	
	public static int makeHash(int p_75830_0_, int p_75830_1_, int p_75830_2_)
    {
        return p_75830_1_ & 255 | (p_75830_0_ & 32767) << 8 | (p_75830_2_ & 32767) << 24 | (p_75830_0_ < 0 ? Integer.MIN_VALUE : 0) | (p_75830_2_ < 0 ? 32768 : 0);
    }
	
	@Override
	public EnumBehaviorState tick() {
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		EntityPlayer playerEnt = Corobot.getPlayerAI().bridgePlayer.getPlayer();
		World worldMC = Minecraft.getMinecraft().theWorld;
		
		int range = 10;
		int rangeY = 5;
		
		if (worldMC.getTotalWorldTime() % 20 == 0) {
			for (int x = -range; x < range; x++) {
				for (int z = -range; z < range; z++) {
					for (int y = -rangeY; y < rangeY; y++) {
						int xx = MathHelper.floor_double(playerEnt.posX)+x;
						int yy = MathHelper.floor_double(playerEnt.posY)+y;
						int zz = MathHelper.floor_double(playerEnt.posZ)+z;
						Block block = worldMC.getBlock(xx, yy, zz);
						int meta = worldMC.getBlockMetadata(zz, yy, zz);
						
						if (listResources.contains(block)) {
							int hash = makeHash(xx, yy, zz);
							if (!lookupBlocks.containsKey(hash)) {
								IWorldStateProperty prop = new ResourceLocation(new Vector3f(xx, yy, zz), block, meta);
								this.getBlackboard().getWorldMemory().getProperties().add(prop);
								lookupBlocks.put(hash, prop);
								System.out.println("adding " + block + " - " + lookupBlocks.size());
							}
						} else if (listMachines.contains(block)) {
							int hash = makeHash(xx, yy, zz);
							if (!lookupBlocks.containsKey(hash)) {
								IWorldStateProperty prop = new MachineLocation(new Vector3f(xx, yy, zz), block);
								this.getBlackboard().getWorldMemory().getProperties().add(prop);
								lookupBlocks.put(hash, prop);
								System.out.println("adding " + block + " - " + lookupBlocks.size());
							}
						}
					}
				}
			}
		}
		
		return super.tick();
	}
}
