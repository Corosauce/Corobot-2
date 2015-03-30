package corobot.ai.memory.pieces;

import javax.vecmath.Vector3f;

import com.corosus.ai.minigoap.IWorldStateProperty;

public class WorldLocation implements IWorldStateProperty {

	private Vector3f pos;
	
	public WorldLocation(Vector3f pos) {
		this.pos = pos;
	}

	public Vector3f getPos() {
		return pos;
	}

	public void setPos(Vector3f pos) {
		this.pos = pos;
	}

	@Override
	public boolean canEffectSatisfyPrecondition(IWorldStateProperty precondition) {
		//this method probably wont be used at this level, we dont need to actually compare exact locations, we optimize closest later
		return true;
	}
	
	
	
}
