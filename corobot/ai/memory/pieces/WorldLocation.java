package corobot.ai.memory.pieces;

import javax.vecmath.Vector3f;

import net.minecraft.nbt.NBTTagCompound;

import com.corosus.ai.minigoap.IWorldStateProperty;

public class WorldLocation implements IWorldStateProperty {

	private Vector3f pos;
	
	public WorldLocation() {
		super();
	}

	@Override
	public void write(Object obj) {
		NBTTagCompound nbt = (NBTTagCompound) obj;
		nbt.setFloat("posX", pos.x);
		nbt.setFloat("posY", pos.y);
		nbt.setFloat("posZ", pos.z);
	}

	@Override
	public void read(Object obj) {
		NBTTagCompound nbt = (NBTTagCompound) obj;
		pos = new Vector3f(nbt.getFloat("posX"), nbt.getFloat("posY"), nbt.getFloat("posZ"));
	}
	
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

	/**
	 * We probably dont need to overload this method on child classes, since no 2 locations can have same pos vec, so this comparison is enough i think
	 */
	@Override
	public boolean isSame(IWorldStateProperty prop) {
		if (prop instanceof WorldLocation) {
			WorldLocation propLoc = (WorldLocation) prop;
			return pos.equals(propLoc.pos);
		}
		return false;
	}

	@Override
	public void setAmount(int amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getAmount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getReuseAmount() {
		return 9999;
	}

	@Override
	public void setReuseAmount(int amount) {
		//no
	}

	@Override
	public boolean canMergeWithSame() {
		return false;
	}
	
	
	
}
