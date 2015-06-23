package corobot.ai;

import javax.vecmath.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.corosus.ai.AIBTAgent;
import com.corosus.ai.Blackboard;

import corobot.ai.memory.PlayerMemory;
import corobot.ai.memory.pieces.BlockLocation;

public class BlackboardImpl extends Blackboard {
	
	private ItemStack itemToPickup;
	private BlockLocation blockLocationToMine;
	private BlockLocation blockLocationToPlace;
	private Block blockToMine;
	private int metaToMine;
	private Vector3f pathConstructEnd;
	
	public BlockLocation getBlockLocationToPlace() {
		return blockLocationToPlace;
	}

	public void setBlockLocationToPlace(BlockLocation blockLocationToPlace) {
		this.blockLocationToPlace = blockLocationToPlace;
	}

	public Vector3f getPathConstructEnd() {
		return pathConstructEnd;
	}

	public void setPathConstructEnd(Vector3f pathConstructEnd) {
		this.pathConstructEnd = pathConstructEnd;
	}

	public Block getBlockToMine() {
		return blockToMine;
	}

	public void setBlockToMine(Block blockToMine) {
		this.blockToMine = blockToMine;
	}

	public int getMetaToMine() {
		return metaToMine;
	}

	public void setMetaToMine(int metaToMine) {
		this.metaToMine = metaToMine;
	}

	public BlockLocation getBlockLocationToMine() {
		return blockLocationToMine;
	}

	public void setBlockLocationToMine(BlockLocation blockLocation) {
		this.blockLocationToMine = blockLocation;
	}

	public ItemStack getItemToPickup() {
		return itemToPickup;
	}

	public void setItemToPickup(ItemStack itemToPickup) {
		this.itemToPickup = itemToPickup;
	}

	public BlackboardImpl(AIBTAgent parAgent) {
		super(parAgent);
		this.setWorldMemory(new PlayerMemory(this));
	}

	public PlayerMemory getPlayerMemory() {
		return (PlayerMemory) getWorldMemory();
	}
	
	

}
