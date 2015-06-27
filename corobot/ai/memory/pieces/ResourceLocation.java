package corobot.ai.memory.pieces;

import javax.vecmath.Vector3f;

import com.corosus.ai.minigoap.IWorldStateProperty;

import net.minecraft.block.Block;

public class ResourceLocation extends BlockLocation {

	private int reuseAmount = 1;
	
	public ResourceLocation(Vector3f pos, Block block) {
		super(pos, block);
	}

	public ResourceLocation(Vector3f pos, Block block, int meta) {
		super(pos, block, meta);
	}
	
	@Override
	public int getReuseAmount() {
		return reuseAmount;
	}
	
	@Override
	public void setReuseAmount(int amount) {
		reuseAmount = amount;
	}
}
