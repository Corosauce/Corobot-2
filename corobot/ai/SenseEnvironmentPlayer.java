package corobot.ai;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.corosus.ai.AIBTAgent;
import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.leaf.LeafNode;
import com.corosus.entity.IEntity;

import corobot.bridge.TargetBridge;

public class SenseEnvironmentPlayer extends LeafNode {

	private AIBTAgent agent;
    private Blackboard blackboard;

	public SenseEnvironmentPlayer(BehaviorNode parParent, AIBTAgent parAgent) {
		super(parParent);
		blackboard = parAgent.getBlackboard();
		agent = parAgent;
	}

	@Override
	public EnumBehaviorState tick() {

        //Target attack maintenance
        IEntity target = blackboard.getTargetAttack();
        if (target != null) {
            if (target.getIsDead()) {
                blackboard.setTargetAttack(null);
            }
        }

        //Target avoid maintenance
        target = blackboard.getTargetAvoidClosest();
        if (target != null) {
            if (target.getIsDead()) {
                blackboard.setTargetAvoidClosest(null);
            }
        }

        updateStateFlags();
		
		return super.tick();
	}

    public void updateStateFlags() {
    	
    	senseEnemiesToAttack();
    	senseThreatsToAvoid();
    	
        //Safety checking
        if (!safetyCheck()) {
            blackboard.shouldTrySurvival().set(true);
        } else {
            blackboard.shouldTrySurvival().set(false);
        }

        boolean hasOrders = blackboard.getAgent().getBtTemplate().ordersHandler.hasOrders();

        //Check for active orders
        blackboard.shouldFollowOrders().set(hasOrders && blackboard.getAgent().getProfile().shouldFollowOrders());

        //Check if should be fighting state
        blackboard.isFighting().set(!blackboard.shouldTrySurvival().get() && blackboard.getTargetAttack() != null);
    }

    public boolean safetyCheck() {
        return !blackboard.getAgent().getProfile().shouldTrySurvival();
    }
    
    public void senseEnemiesToAttack() {
    	EntityPlayer player = ((PlayerAI)agent.getActor()).bridgePlayer.getPlayer();
    	World world = player.worldObj;
    	
    	float huntRange = 100;
    	boolean xRay = false;
    	
    	Entity clEnt = null;
		Entity clPickup = null;
		float closest = 9999F;
		float closestPickup = 9999F;
    	List list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.expand(huntRange, huntRange/2, huntRange));
        for(int j = 0; j < list.size(); j++)
        {
            Entity ent = (Entity)list.get(j);
            
            if (isEnemy(ent)) {
            	if ((xRay || player.canEntityBeSeen(ent)) && (player.posY > ent.posY-3 && player.posY < ent.posY+8)) {
            		if (this.agent.getProfile().canWinScenario(new TargetBridge(ent))/* && ent instanceof EntityPlayer*/) {
            			float dist = player.getDistanceToEntity(ent);
            			if (dist < closest) {
            				closest = dist;
            				clEnt = ent;
            			}
            		}
            	}
            }
        }
        
        if (clEnt != null) {
        	IEntity ent = this.blackboard.getTargetAttack();
        	if (ent instanceof TargetBridge) {
        		((TargetBridge) ent).cleanup();
        	}
        	this.blackboard.setTargetAttack(new TargetBridge(clEnt));
        }
    }
    
    public boolean isEnemy(Entity ent) {
    	return ent instanceof IMob;
    }
    
    public void senseThreatsToAvoid() {
    	
    }
}
