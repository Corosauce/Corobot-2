package corobot.ai.memory.helper;

import javax.vecmath.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import corobot.Corobot;

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
							return new Vector3f(x, y, z);
						}
					}
				}
			}
		}
		return null;
	}
}
