package corobot.ai.profile;

import net.minecraft.entity.player.EntityPlayer;

import com.corosus.ai.AIBTAgent;
import com.corosus.ai.profile.ProfileBase;
import com.corosus.entity.IEntity;

import corobot.Corobot;
import corobot.ai.behaviors.ScanEnvironmentForNeededBlocks;
import corobot.ai.behaviors.StayAboveWater;
import corobot.ai.behaviors.combat.AvoidClosestThreat;
import corobot.ai.behaviors.combat.TrackAndAttackEntity;
import corobot.ai.behaviors.misc.EventlessStateTracker;
import corobot.ai.behaviors.misc.OpenGUIChatWhenNeeded;
import corobot.ai.behaviors.misc.RespawnIfDead;
import corobot.ai.behaviors.survival.EatWhenNeeded;
import corobot.ai.behaviors.yd.OrdersYDScript;
import corobot.ai.memory.helper.HelperItemUsing;
import corobot.ai.memory.helper.HelperItemUsing.ItemUse;

public class ProfilePlayer extends ProfileBase {

	public ProfilePlayer(AIBTAgent parAgent) {
		super(parAgent);
	}
	
	@Override
	public void init() {
		super.init();
		
		boolean ydMode = false;
		
		getBtAttack().add(new TrackAndAttackEntity(getBtAttack(), this.getAgent().getBlackboard()));

		getBtSurvive().add(new AvoidClosestThreat(getBtSurvive(), getAgent().getBlackboard()));
		
		
		if (!ydMode) {
			//BuildHouse will probably get moved to MasterPlanSequence
			//but it also needs to be able to get called if house is detected broken, so it needs to interrupt the sequence chain... hmm
			//getBtIdle().add(new IdleWander(getAgent().getBtTemplate().btExtras, this.getAgent().getBlackboard()));
			//getBtIdle().add(new BuildHouse(getAgent().getBtTemplate().btExtras, this.getAgent().getBlackboard()));
			
		}
		
		getAgent().getBtTemplate().btExtras.add(new StayAboveWater(getAgent().getBtTemplate().btExtras, this.getAgent().getBlackboard()));
		getAgent().getBtTemplate().btExtras.add(new RespawnIfDead(getAgent().getBtTemplate().btExtras, this.getAgent().getBlackboard()));
		getAgent().getBtTemplate().btExtras.add(new OpenGUIChatWhenNeeded(getAgent().getBtTemplate().btExtras, this.getAgent().getBlackboard()));
		
		getAgent().getBtTemplate().btExtras.add(new ScanEnvironmentForNeededBlocks(getAgent().getBtTemplate().btExtras, this.getAgent().getBlackboard()));
		getAgent().getBtTemplate().btExtras.add(new EatWhenNeeded(getAgent().getBtTemplate().btExtras, this.getAgent().getBlackboard()));
		getAgent().getBtTemplate().btExtras.add(new EventlessStateTracker(getAgent().getBtTemplate().btExtras, this.getAgent().getBlackboard()));
		

		
		//this makes sense for YD, but not so much for MasterPlanSequence since it will use GOAP
		//UNLESS goap use is a sub sequence within MasterPlanSequence
		//i guess we should plan out more how MasterPlanSequence will work its magic first
		if (ydMode) {
			getAgent().getBtTemplate().ordersHandler.setOrders(new OrdersYDScript(getAgent().getBtTemplate().btExtras, getAgent().getBlackboard()));
		} else {
			getAgent().getBtTemplate().ordersHandler.setOrders(new MasterPlanSequence(getAgent().getBtTemplate().btExtras, getAgent().getBlackboard()));
		}
		
		/*BehaviorNode tasks = getAgent().getBtTemplate().ordersHandler.getOrders();
		tasks.add(new JumpForBoredom(tasks, getAgent().getBlackboard()));*/
	}
	
	@Override
	public boolean shouldFollowOrders() {
		
		//we probably want to implement a priority system and check it here
		//priority of ordertask vs priority of enemy to kill, creeper would be higher priority than zombie for example? (creeper to avoid potentially)
		//also ordertask varies in priority depending on active task, eg: ???
		
		return this.getAgent().getBlackboard().getTargetAttack() == null;
	}
	
	@Override
	public boolean shouldTrySurvival() {
		/*System.out.println("temp force survival on");
		return true;*/
		EntityPlayer player = Corobot.playerAI.bridgePlayer.getPlayer();
		if (player.getHealth() < player.getMaxHealth() / 2) return true;
		if (HelperItemUsing.isUsing(ItemUse.FOOD)) return true;
		return super.shouldTrySurvival();
	}
	
	@Override
	public boolean isEnemy(IEntity parActor) {
		return super.isEnemy(parActor);
	}
	
	@Override
	public boolean canWinScenario(IEntity parActor) {

		Corobot.getPlayerAI().updateCache();
    	
    	return true;
	}

}
