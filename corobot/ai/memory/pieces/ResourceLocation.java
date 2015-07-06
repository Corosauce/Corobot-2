package corobot.ai.memory.pieces;

import javax.vecmath.Vector3f;

import com.corosus.ai.minigoap.IWorldStateProperty;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;

public class ResourceLocation extends BlockLocation {

	private int reuseAmount = 1;
	
	public ResourceLocation() {
		
	}

	@Override
	public void write(Object obj) {
		super.write(obj);
		NBTTagCompound nbt = (NBTTagCompound) obj;
		nbt.setInteger("reuseAmount", reuseAmount);
	}

	@Override
	public void read(Object obj) {
		super.read(obj);
		NBTTagCompound nbt = (NBTTagCompound) obj;
		reuseAmount = nbt.getInteger("reuseAmount");
		
	}
	
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
