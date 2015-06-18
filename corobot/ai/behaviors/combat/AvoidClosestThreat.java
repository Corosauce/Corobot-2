package corobot.ai.behaviors.combat;

import javax.vecmath.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;

import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.leaf.LeafNode;
import com.corosus.entity.IEntity;

import corobot.Corobot;
import corobot.ai.memory.helper.HelperPath;
import corobot.ai.memory.helper.HelperPath.Repaths;
import corobot.bridge.TargetBridge;

public class AvoidClosestThreat extends LeafNode {
	
	public AvoidClosestThreat(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
	}

	@Override
	public EnumBehaviorState tick() {
		
		float attackRange = 4F;
		float stopPathRange = 4F;
		
		IEntity player = this.getBlackboard().getAgent().getActor();
		IEntity target = this.getBlackboard().getTargetAttack();
		if (target != null) {
			//unused instance check
			if (target instanceof TargetBridge) {
				Entity targetEnt = ((TargetBridge)target).target;
				
				fleeFrom(targetEnt, false);
			}
		}
		
		return super.tick();
	}
	
	public void fleeFrom(Entity fleeFrom, boolean lifeAtRisk) {
		
		int maxPFRange = 16;
		
		IEntity player = this.getBlackboard().getAgent().getActor();
		
		//ent field rerouting!
		EntityLivingBase ent = Corobot.getPlayerAI().bridgePlayer.getPlayer();
		
		
		
		/*this.faceEntity(fleeFrom, 180F, 180F);
		//this.rotationYaw += 180;
		
		double d1 = posX - fleeFrom.posX;
        double d2 = posZ - fleeFrom.posZ;
        float f2 = (float)((Math.atan2(d2, d1) * 180D) / 3.1415927410125732D) - 90F;
        float f3 = f2 - rotationYaw;
        
        rotationYaw = updateRotation2(rotationYaw, f3, 360F);*/
		
		double d = fleeFrom.posX - ent.posX;
        double d1 = fleeFrom.posZ - ent.posZ;
        /*for (; d * d + d1 * d1 < 0.0001D; d1 = (Math.random() - Math.random()) * 0.01D)
        {
            d = (Math.random() - Math.random()) * 0.01D;
        }*/
        float f = MathHelper.sqrt_double(d * d + d1 * d1);

        //knockBack(entity, i, d, d1);
        
        float yaw = (float)((Math.atan2(d1, d) * 180D) / 3.1415927410125732D)/* - ent.rotationYaw*/;
		
        int randAngleRange = 15;
        
		//float look = 0;//ent.worldObj.rand.nextInt(randAngleRange)-randAngleRange/2;
        //int height = 10;
        double dist = ent.worldObj.rand.nextInt(2)+8;
        
        //dist adjuster
        if (fleeFrom instanceof EntityCreeper) {
        	dist = ent.worldObj.rand.nextInt(8)+8;
        } else if (fleeFrom instanceof EntitySkeleton || fleeFrom instanceof EntityWitch || fleeFrom instanceof EntityBlaze || fleeFrom instanceof EntityGhast) {
        	dist = ent.worldObj.rand.nextInt(8)+16;
        }
        
        if (lifeAtRisk) {
        	dist = 20;
        }
        
        double distFromTarg = ent.getDistanceToEntity(fleeFrom);
        if (distFromTarg > dist) {
			return;
		}
        
        float pitch = ent.rotationPitch;
        pitch = 0;
        
        int gatherX = 0;//(int)(ent.posX + ((double)(-Math.sin((yaw+look) / 180.0F * 3.1415927F) * Math.cos(pitch / 180.0F * 3.1415927F)) * dist));
        int gatherY = (int)(ent.posY-0.5 + (double)(-MathHelper.sin(pitch / 180.0F * 3.1415927F) * dist) - 0D); //center.posY - 0D;
        int gatherZ = 0;//(int)(ent.posZ + ((double)(Math.cos((yaw+look) / 180.0F * 3.1415927F) * Math.cos(pitch / 180.0F * 3.1415927F)) * dist));
        
        gatherX = (int)(ent.posX - (d / f * dist));
        gatherZ = (int)(ent.posZ - (d1 / f * dist));
        
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
        	//if (ent.worldObj.getTotalWorldTime() % 10 == 0) {
        	if (HelperPath.pathNow(Repaths.FLEE)) {
        		
        		if (ent.onGround || ent.isInWater()) {
        			Block what = ent.worldObj.getBlock(gatherX, finalY, gatherZ);
        			if (what != Blocks.water) {
        				getBlackboard().setMoveToBest(new Vector3f(gatherX, finalY+1, gatherZ));
        				HelperPath.pathed(Repaths.FLEE);
        				Block what2 = ent.worldObj.getBlock(gatherX, finalY+1, gatherZ);
        			} else {
        				getBlackboard().setMoveToBest(new Vector3f(gatherX, finalY+1, gatherZ));
        				HelperPath.pathed(Repaths.FLEE);
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
