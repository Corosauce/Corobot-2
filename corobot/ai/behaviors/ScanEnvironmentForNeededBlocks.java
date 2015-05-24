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
import corobot.ai.memory.helper.HelperBlock;
import corobot.ai.memory.pieces.MachineLocation;
import corobot.ai.memory.pieces.ResourceLocation;

public class ScanEnvironmentForNeededBlocks extends LeafNodeBB {

	
	
	public ScanEnvironmentForNeededBlocks(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
	}
	
	@Override
	public EnumBehaviorState tick() {
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		EntityPlayer playerEnt = Corobot.getPlayerAI().bridgePlayer.getPlayer();
		World worldMC = Minecraft.getMinecraft().theWorld;
		
		int range = 10;
		int rangeY = 2;
		
		if (worldMC.getTotalWorldTime() % 20 == 0) {
			for (int x = -range; x < range; x++) {
				for (int z = -range; z < range; z++) {
					for (int y = -rangeY; y < rangeY; y++) {
						int xx = MathHelper.floor_double(playerEnt.posX)+x;
						int yy = MathHelper.floor_double(playerEnt.posY)+y;
						int zz = MathHelper.floor_double(playerEnt.posZ)+z;
						Block block = worldMC.getBlock(xx, yy, zz);
						int meta = worldMC.getBlockMetadata(zz, yy, zz);
						
						if (HelperBlock.listResources.contains(block)) {
							int hash = HelperBlock.makeHash(xx, yy, zz);
							if (!HelperBlock.lookupBlocks.containsKey(hash)) {
								IWorldStateProperty prop = new ResourceLocation(new Vector3f(xx, yy, zz), block, meta);
								HelperBlock.addEntry(this.getBlackboard().getWorldMemory(), hash, prop);
								//System.out.println("adding " + block + " - " + HelperBlock.lookupBlocks.size());
							}
						} else if (HelperBlock.listMachines.contains(block)) {
							int hash = HelperBlock.makeHash(xx, yy, zz);
							if (!HelperBlock.lookupBlocks.containsKey(hash)) {
								IWorldStateProperty prop = new MachineLocation(new Vector3f(xx, yy, zz), block);
								HelperBlock.addEntry(this.getBlackboard().getWorldMemory(), hash, prop);
								System.out.println("adding " + block + " - " + HelperBlock.lookupBlocks.size());
							}
						}
					}
				}
			}
		}
		
		return super.tick();
	}
}
