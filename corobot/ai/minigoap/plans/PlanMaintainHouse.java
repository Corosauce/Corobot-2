package corobot.ai.minigoap.plans;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.minigoap.PlanPiece;

import corobot.ai.behaviors.misc.TaskBuildHouse;
import corobot.ai.memory.helper.HelperHouse;
import corobot.ai.memory.pieces.HouseLocation;
import corobot.ai.memory.pieces.ItemEntry;
import corobot.ai.memory.pieces.ResourceLocation;
import corobot.ai.memory.pieces.inventory.InventorySourceSelf;

public class PlanMaintainHouse extends PlanPiece {
	
	public BehaviorNode taskHouse;
	
	public PlanMaintainHouse(String planName, Blackboard blackboard) {
		super(planName, blackboard);
		
		this.getEffects().getProperties().add(HelperHouse.effectHouse);
		
		//TODO: make resource scanning use immidiate scanning for grass/dirt to mine, since its everywhere
		
		this.getPreconditions().getProperties().add(new ItemEntry(new ItemStack(HelperHouse.getBlockHouseMaterial(), 64), new InventorySourceSelf()));
	}
	
	public PlanMaintainHouse(PlanPiece obj) {
		super(obj, obj.getBlackboard());
		
		taskHouse = new TaskBuildHouse(this, getBlackboard());
	}
	
	@Override
	public EnumBehaviorState tick() {
		
		//TODO: reset house state on success
		EnumBehaviorState result = taskHouse.tick();
		
		
		return result;
	}

}
