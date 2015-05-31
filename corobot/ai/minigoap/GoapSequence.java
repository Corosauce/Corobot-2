package corobot.ai.minigoap;

import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.tree.Sequence;
import com.corosus.ai.minigoap.PlanGoal;
import com.corosus.ai.minigoap.PlanPiece;
import com.corosus.ai.minigoap.PlanRegistry;

import corobot.ai.minigoap.plans.PlanCraftRecipe;

public class GoapSequence extends Sequence {

	public String endGoalName = "";

	public PlanGoal planGoal = new PlanGoal();
	
	public GoapSequence(BehaviorNode parParent, Blackboard blackboard, String endGoal) {
		super(parParent, blackboard);
		this.endGoalName = endGoal;
	}
	
	public boolean createPlan() {
		PlanPiece endGoal = PlanRegistry.getPlanPieceByNamePartial(endGoalName);
		/*if (endGoal instanceof PlanCraftRecipe) {
			((PlanCraftRecipe) endGoal).setAmountToCraft(64);
		}*/
		return planGoal.createPlan(endGoal, getBlackboard().getWorldMemory());
	}
	
	public void setCreatedPlan() {
		resetChildren();
		getChildren().clear();
		
		for (PlanPiece bh : planGoal.getListPlanPieces()) {
			getChildren().add(bh);
		}
		
	}
	
	@Override
	public EnumBehaviorState tick() {
		return super.tick();
	}
	
	@Override
	public void reset() {
		super.reset();
		planGoal.invalidatePlan();
	}
	
	
}
