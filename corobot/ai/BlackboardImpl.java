package corobot.ai;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.corosus.ai.AIBTAgent;
import com.corosus.ai.Blackboard;

import corobot.ai.memory.PlayerMemory;
import corobot.ai.memory.pieces.BlockLocation;

public class BlackboardImpl extends Blackboard {
	
	private ItemStack itemToPickup;
	private BlockLocation blockLocation;
	private Block blockToMine;
	private int metaToMine;
	
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

	public BlockLocation getBlockLocation() {
		return blockLocation;
	}

	public void setBlockLocation(BlockLocation blockLocation) {
		this.blockLocation = blockLocation;
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
