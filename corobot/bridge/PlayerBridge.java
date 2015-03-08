package corobot.bridge;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import com.corosus.util.VecUtil;

import corobot.ai.PlayerAI;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;

public class PlayerBridge {

	/** Class that links minecraft code with CoroLib code **/
	public EntityPlayer lastPlayer;
	
	public PlayerAI playerAI;
	
	public PlayerBridge(PlayerAI playerAI) {
		this.playerAI = playerAI;
	}
	
	/*public void init() {
		EntityPlayer player = getPlayer();
		if (player == null) return;
	}*/
	
	public void newPlayerInit(EntityPlayer player) {
		//init whatever here
		
		lastPlayer = player;
	}
	
	public EntityPlayer getPlayer() {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (player != lastPlayer) {
			newPlayerInit(player);
		}
		return player;
	}
	
	public void tickUpdate() {
		float speed = 0.2F;
		Vector3f moveTo = playerAI.getMvtvec();
		if (moveTo != null) {
			float dist = VecUtil.getDistSqrdXZ(moveTo, new Vector3f((float)getPlayer().posX, (float)getPlayer().boundingBox.minY, (float)getPlayer().posZ));
			float motionX = moveTo.x - (float)getPlayer().posX;
			float motionY = moveTo.y - (float)getPlayer().boundingBox.minY;
			float motionZ = moveTo.z - (float)getPlayer().posZ;
			
			float rX = motionX / dist * speed;
			float rY = motionY / dist * speed;
			float rZ = motionZ / dist * speed;
			
			getPlayer().motionX = rX;
			//getPlayer().motionY = rY;
			getPlayer().motionZ = rZ;
			
			if (motionY > 0.1) {
				if (getPlayer().isInWater()) {
					getPlayer().motionY += 0.08;
				} else if (getPlayer().onGround) {
					getPlayer().jump();
				}
			}
			
			System.out.println("movement! - " + rX + ", " + rY + ", " + rZ);
		}
	}
	
	public List<Vector3f> computePath(Vector3f moveTo) {
		EntityPlayer player = getPlayer();
		if (player == null) return new ArrayList<Vector3f>();
		
		PathEntity mcPath = player.worldObj.getEntityPathToXYZ(player, MathHelper.floor_double(moveTo.x), MathHelper.floor_double(moveTo.y), MathHelper.floor_double(moveTo.z), 512F, true, true, false, false);
		List<Vector3f> path = convertPath(mcPath);
		return path;
	}
	
	public List<Vector3f> convertPath(PathEntity parPathEnt) {
		List<Vector3f> listNewPath = new ArrayList<Vector3f>();
		
		if (parPathEnt != null) {
			while (parPathEnt.getCurrentPathIndex() < parPathEnt.getCurrentPathLength()) {
				PathPoint point = parPathEnt.getPathPointFromIndex(parPathEnt.getCurrentPathIndex());
				Vector3f vec = new Vector3f(point.xCoord, point.yCoord, point.zCoord);
				vec.add(new Vector3f(0.5F, 0F, 0.5F));
				listNewPath.add(vec);
				parPathEnt.incrementPathIndex();
			}
		}
		
		return listNewPath;
		
	}
	
	
	
}
