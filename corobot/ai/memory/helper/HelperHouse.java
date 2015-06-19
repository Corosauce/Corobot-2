package corobot.ai.memory.helper;

import javax.vecmath.Vector3f;

import com.corosus.ai.AIBTAgent;
import com.corosus.ai.Blackboard;
import com.corosus.ai.minigoap.IWorldStateProperty;
import com.corosus.entity.IEntity;
import com.corosus.world.IWorld;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import corobot.Corobot;
import corobot.ai.memory.pieces.HouseLocation;

public class HelperHouse {

	/**
	 * The purpose of this class is to store anything home related
	 * - home x y z
	 * - home size
	 * - door to home entrance
	 * - possible future schematic
	 * Though HelperBlock will have machine locations which will probably be in our home
	 * 
	 * Make a square or circle shaped house
	 * - coords of walls are based off of home x y z + size
	 */
	
	public static Vector3f posHome;
	public static int sizeRadiusHome = 5;
	public static int sizeHeightHome = 5;
	
	private static Block blockHouseMaterial = Blocks.dirt;
	
	//this is used in sensing for updating if we need to maintain house or not, to helper goal planner know 
	public static HouseLocation effectHouse = new HouseLocation(HelperHouse.posHome);
	
	public static Block getBlockHouseMaterial() {
		return blockHouseMaterial;
	}

	public static void setBlockHouseMaterial(Block blockHouseMaterial) {
		HelperHouse.blockHouseMaterial = blockHouseMaterial;
	}

	public static void init() {
		
		//temp
		posHome = new Vector3f(132, 64, 251);
		
		initLoad();
	}
	
	public static void initLoad() {
		//sqlite db data load
	}
	
	public static boolean trySetHome(Vector3f pos) {
		Minecraft mc = Minecraft.getMinecraft();
		World world = mc.theWorld;
		EntityPlayer player = Corobot.playerAI.bridgePlayer.getPlayer();
		
		int maxYHill = 3;
		int yStart = (int) pos.y;
		
		while (!world.getBlock(MathHelper.floor_double(pos.x), yStart, MathHelper.floor_double(pos.z)).isBlockNormalCube()) {
			yStart--;
		}
		
		//should set it to first non solid block above ground
		yStart++;
		
		boolean testFail = false;
		
		for (int x = MathHelper.floor_double(pos.x - sizeRadiusHome); x < pos.x + sizeRadiusHome; x++) {
			if (testFail) break;
			for (int z = MathHelper.floor_double(pos.z - sizeRadiusHome); z < pos.z + sizeRadiusHome; z++) {
				if (testFail) break;
				int yTest = 0;
				while (world.getBlock(x, yStart+yTest, z).isBlockNormalCube()) {
					yTest++;
					if (yTest > maxYHill) {
						testFail = true;
						break;
					}
				}
			}
		}
		
		if (testFail) return false;
		
		posHome = new Vector3f(pos);
		
		return true;
	}
	
	public static Vector3f getBlockToBuild() {
		
		//sizeHeightHome = 5;
		
		//TODO: account for door
		
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		Blackboard bb = agent.getBlackboard();
		
		Minecraft mc = Minecraft.getMinecraft();
		World world = mc.theWorld;
		EntityPlayer player = Corobot.playerAI.bridgePlayer.getPlayer();
		for (int y = (int) posHome.y; y < (int)(posHome.y + sizeHeightHome); y++) {
			for (int x = MathHelper.floor_double(posHome.x - sizeRadiusHome); x <= posHome.x + sizeRadiusHome; x++) {
				for (int z = MathHelper.floor_double(posHome.z - sizeRadiusHome); z <= posHome.z + sizeRadiusHome; z++) {
					if (x == MathHelper.floor_double(posHome.x - sizeRadiusHome) ||
							x == MathHelper.floor_double(posHome.x + sizeRadiusHome) ||
							z == MathHelper.floor_double(posHome.z - sizeRadiusHome) ||
							z == MathHelper.floor_double(posHome.z + sizeRadiusHome)) {
						Block block = world.getBlock(x, y, z);
						if (!block.isBlockNormalCube()) {
							//account for door here
							if (x == posHome.x + 5 && z == posHome.z + 0 && (y == posHome.y + 0 || y == posHome.y + 1)) {
								
							} else {
								return new Vector3f(x, y, z);
							}
						}
					}
				}
			}
		}
		if (!bb.getWorldMemory().getProperties().contains(HelperHouse.effectHouse)) {
			bb.getWorldMemory().getProperties().add(HelperHouse.effectHouse);
		}
		return null;
	}
	
	public static boolean shouldMine(Vector3f pos) {
		
		int buffer = 10;
		
		if (pos.x >= posHome.x - (sizeRadiusHome + buffer) && pos.x <= posHome.x + (sizeRadiusHome + buffer) && 
				pos.z >= posHome.z - (sizeRadiusHome + buffer) && pos.z <= posHome.z + (sizeRadiusHome + buffer) && 
				pos.y >= posHome.y - (sizeHeightHome + buffer) && pos.y <= posHome.y + (sizeHeightHome + buffer)) {
			return false;
		}
		
		return true;
	}
}
