package corobot.ai.memory.pieces;

import javax.vecmath.Vector3f;

import com.corosus.ai.minigoap.IWorldStateProperty;

import net.minecraft.block.Block;

public class BlockLocation extends WorldLocation {

	private Block block;
	private int meta = -1;
	
	public BlockLocation(Vector3f pos, Block block) {
		super(pos);
		this.block = block;
	}
	
	public BlockLocation(Vector3f pos, Block block, int meta) {
		super(pos);
		this.block = block;
		this.meta = meta;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}
	
	public int getMeta() {
		return meta;
	}

	public void setMeta(int meta) {
		this.meta = meta;
	}

	@Override
	public boolean canEffectSatisfyPrecondition(IWorldStateProperty precondition) {
		if (precondition instanceof BlockLocation) {
			BlockLocation precond = (BlockLocation) precondition;
			return precond.getBlock() == getBlock() && (meta == -1 || precond.getMeta() == getMeta());
		} else {
			return false;
		}
	}

}
