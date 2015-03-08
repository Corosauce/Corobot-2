package corobot.ai.profile;

import com.corosus.ai.AIBTAgent;
import com.corosus.ai.profile.ProfileBase;
import com.corosus.entity.IEntity;

import corobot.ai.behaviors.TrackAndAttackEntity;

public class ProfilePlayer extends ProfileBase {

	public ProfilePlayer(AIBTAgent parAgent) {
		super(parAgent);
	}
	
	@Override
	public void init() {
		super.init();
		
		getAgent().getBtTemplate().btAttack.add(new TrackAndAttackEntity(getAgent().getBtTemplate().btAttack, this.getAgent().getBlackboard()));
	}
	
	@Override
	public boolean shouldFollowOrders() {
		
		//we probably want to implement a priority system and check it here
		//priority of ordertask vs priority of enemy to kill, creeper would be higher priority than zombie for example? (creeper to avoid potentially)
		//also ordertask varies in priority depending on active task, eg: ???
		
		return this.getAgent().getBlackboard().getTargetAttack() == null;
	}
	
	@Override
	public boolean isEnemy(IEntity parActor) {
		return super.isEnemy(parActor);
	}

}
