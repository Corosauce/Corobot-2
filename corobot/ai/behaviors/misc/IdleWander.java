package corobot.ai.behaviors.misc;

import javax.vecmath.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;

import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.leaf.LeafNodeBB;
import com.corosus.entity.IEntity;

import corobot.Corobot;
import corobot.c_AIP;
import corobot.bridge.TargetBridge;

public class IdleWander extends LeafNodeBB {
	
	public IdleWander(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
	}

	@Override
	public EnumBehaviorState tick() {
		
		//for some reason proper idle BT isnt triggering, diagnose why
		
		Corobot.dbg("wander!");
		wander();
		
		return super.tick();
	}
	
	public void wander() {
		
		int maxPFRange = 16;
		
		IEntity player = this.getBlackboard().getAgent().getActor();
		
		//ent field rerouting!
		EntityLivingBase ent = Corobot.getPlayerAI().bridgePlayer.getPlayer();
		
		
		if (ent.worldObj.getTotalWorldTime() % 40 != 0) {
			return;
		}
        
        int range = 30;
        
        int gatherX = (int) (ent.posX + ent.worldObj.rand.nextInt(range) - ent.worldObj.rand.nextInt(range/2));
        int gatherY = (int) ent.posY;
        int gatherZ = (int) (ent.posZ + ent.worldObj.rand.nextInt(range) - ent.worldObj.rand.nextInt(range/2));
        
        Block id = ent.worldObj.getBlock(gatherX, gatherY, gatherZ);
        Block id2 = ent.worldObj.getBlock(gatherX, gatherY+1, gatherZ);
        
        int offset = 0;
        
        int finalY = gatherY;
        
        while (offset < 10) {
        	if ((id != Blocks.air && id != Blocks.tallgrass) && id2 == Blocks.air) {
        		finalY += offset;
        		break;
        	}
        	
        	//id = ent.worldObj.getBlockId(gatherX, gatherY+offset++, gatherZ);
        	id = ent.worldObj.getBlock(gatherX, gatherY-offset, gatherZ);
        	id2 = ent.worldObj.getBlock(gatherX, gatherY-offset+1, gatherZ);
        	
        	if ((id != Blocks.air && id != Blocks.tallgrass) && id2 == Blocks.air) {
        		finalY -= offset;
        		break;
        	}
        	
        	//id = ent.worldObj.getBlockId(gatherX, gatherY+offset++, gatherZ);
        	id = ent.worldObj.getBlock(gatherX, gatherY+offset, gatherZ);
        	id2 = ent.worldObj.getBlock(gatherX, gatherY+offset+1, gatherZ);
        	
        	offset++;
        }
        
        if (offset < 10) {
        	//System.out.println("flee");
        	//if (c_AIP.i.pathToEntity == null) {
        	if (ent.worldObj.getTotalWorldTime() % 10 == 0) {
        		if (ent.onGround || ent.isInWater()) {
        			Block what = ent.worldObj.getBlock(gatherX, finalY, gatherZ);
        			if (what != Blocks.water) {
        				player.setMoveTo(new Vector3f(gatherX, finalY+1, gatherZ));
        				Block what2 = ent.worldObj.getBlock(gatherX, finalY+1, gatherZ);
        			} else {
        				player.setMoveTo(new Vector3f(gatherX, finalY+1, gatherZ));
        				Block what2 = ent.worldObj.getBlock(gatherX, finalY+1, gatherZ);
        				System.out.println("final flee block: " + what2);
        			}
        			
        			
        			
        		}
        	}
        		//c_AIP.i.walkTo(ent, gatherX, finalY, gatherZ, maxPFRange , 600, -1);
        	//}
        	//this.walkTo(this, homeX, homeY, homeZ, maxPFRange, 600);
        } else {
        	//System.out.println("flee failed");
        	//c_AIP.i.walkTo(ent, c_AIP.i.homeX, c_AIP.i.homeY, c_AIP.i.homeZ, maxPFRange, 600, -1);
        }
	}

}
