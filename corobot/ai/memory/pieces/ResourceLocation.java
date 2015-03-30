package corobot.ai.memory.pieces;

import javax.vecmath.Vector3f;

import com.corosus.ai.minigoap.IWorldStateProperty;

import net.minecraft.block.Block;

public class ResourceLocation extends BlockLocation {

	public ResourceLocation(Vector3f pos, Block block) {
		super(pos, block);
	}

}
