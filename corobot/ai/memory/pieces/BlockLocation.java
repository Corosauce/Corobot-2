package corobot.ai.memory.pieces;

import javax.vecmath.Vector3f;

import com.corosus.ai.minigoap.IWorldStateProperty;

import net.minecraft.block.Block;

public class BlockLocation extends WorldLocation {

	private Block block;
	
	public BlockLocation(Vector3f pos, Block block) {
		super(pos);
		this.block = block;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}
	
	@Override
	public boolean canEffectSatisfyPrecondition(IWorldStateProperty precondition) {
		if (precondition instanceof BlockLocation) {
			BlockLocation precond = (BlockLocation) precondition;
			return precond.getBlock() == getBlock();
		} else {
			return false;
		}
	}

}
