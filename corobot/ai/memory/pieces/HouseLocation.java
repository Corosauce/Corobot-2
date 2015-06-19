package corobot.ai.memory.pieces;

import javax.vecmath.Vector3f;

import com.corosus.ai.minigoap.IWorldStateProperty;

import net.minecraft.block.Block;

public class HouseLocation extends WorldLocation {
	
	public HouseLocation(Vector3f pos) {
		super(pos);
	}
	
	@Override
	public boolean canEffectSatisfyPrecondition(IWorldStateProperty precondition) {
		return precondition instanceof HouseLocation;
	}

}
