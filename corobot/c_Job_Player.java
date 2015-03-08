package corobot;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.MathHelper;

import java.util.List;

import CoroAI.entity.JobBase;
import CoroAI.entity.JobManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class c_Job_Player extends JobBase {
	
	
	public Entity lastFleeEnt;

	public float maxPFRange = 96F;
	
	public c_Job_Player(JobManager jm) {
		super(jm);
	}
	
	@Override
	public void onJobRemove() {
		if (this.ent.fishEntity != null) {
			this.ent.fishEntity.catchFish();
		}
	}
	
	@Override
	public boolean shouldExecute() {
		return true;
	}
	
	@Override
	public boolean shouldContinue() {
		return true;
	}
	

	@Override
	public void onLowHealth() {
		
	}
	
	@Override
	public void onIdleTick() {
		//super.onIdleTick();
	}

	@Override
	public void setJobItems() {
		
		//c_CoroAIUtil.setItems_JobGather(ent);
		
	}
	
	@Override
	public boolean avoid(boolean actOnTrue) {
		return avoid(actOnTrue, false, 20F, true);
	}
	
	
	@Override
	public boolean checkHunger() {
		if (c_AIP.i.player.getFoodStats().getFoodLevel() <= 17) {
			//System.out.println("OH: " + + fakePlayer.foodStats.getFoodLevel()); 
			if (c_AIP.i.eat()) {
				//System.out.println("NH: " + fakePlayer.foodStats.getFoodLevel());
			} else {
				//fallback();
				//if (jm.getJob() != EnumJob.FINDFOOD) {
					//ent.swapJob(EnumJob.FINDFOOD);
					return true;
				//}
			}
			//try heal
		}
		return false;
	}
	
	public boolean avoid(boolean actOnTrue, boolean justThreats, float parRange, boolean lifeAtRisk) {
		//ent field rerouting!
		EntityLiving ent = c_AIP.i.player;
		
		Entity clEnt = null;
		float closest = 9999F;
		
		Entity clRisk = null;
		float closestRisk = 9999F;
		
		if (lastFleeEnt != null && lastFleeEnt.isDead) { lastFleeEnt = null; }
		
		float range = parRange;
		
    	List list = ent.worldObj.getEntitiesWithinAABBExcludingEntity(ent, ent.boundingBox.expand(range, range/2, range));
        for(int j = 0; j < list.size(); j++)
        {
            Entity entity1 = (Entity)list.get(j);
            if(!entity1.isDead && ((c_AIP.i.isEnemy(entity1) && (c_AIP.i.isMeleeUser(entity1) || lifeAtRisk) && !justThreats) || (justThreats && c_AIP.i.isFeared(entity1))))
            {
            	if (((EntityLiving) entity1).canEntityBeSeen(ent) || justThreats) {
            		//if (sanityCheck(entity1)) {
            			float dist = ent.getDistanceToEntity(entity1);
            			if (dist < closest) {
            				closest = dist;
            				clEnt = entity1;
            			}
            			
            			if (c_AIP.i.isRangedUser(entity1) || c_AIP.i.isThreat(entity1) || entity1 instanceof EntityEnderman) {
            				if (dist < closestRisk) {
            					closestRisk = dist;
            					clRisk = entity1;
                			}
            			}
	            		
	            		//found = true;
	            		//break;
            		//}
            		//this.hasAttacked = true;
            		//getPathOrWalkableBlock(entity1, 16F);
            	}
            }
        }
        if (clRisk != null) {
        	lastFleeEnt = clRisk;
    		if (actOnTrue) fleeFrom(clRisk, lifeAtRisk);
        } else if (clEnt != null) {
        	//if (clEnt != lastFleeEnt) {
        		lastFleeEnt = clEnt;
        		if (actOnTrue) fleeFrom(clEnt, lifeAtRisk);
        	//}
        } else if (c_AIP.i.pathToEntity == null && lastFleeEnt != null) {
    		if (actOnTrue) fleeFrom(lastFleeEnt, lifeAtRisk);
        }
        
        //no idle wander for now
		if (lastFleeEnt != null) {
			if (lastFleeEnt.isDead) { lastFleeEnt = null; }
			if (lastFleeEnt.getDistanceToEntity(ent) > parRange + 5) lastFleeEnt = null;
		} else {
			//setJobState(EnumJobState.IDLE);
		}
		
		c_AIP.i.jobFleeFrom = lastFleeEnt;
        
        if (clEnt != null) return true;
        return false;
	}
	
	public void fleeFrom(Entity fleeFrom, boolean lifeAtRisk) {
		//ent field rerouting!
		EntityLiving ent = c_AIP.i.player;
		/*this.faceEntity(fleeFrom, 180F, 180F);
		//this.rotationYaw += 180;
		
		double d1 = posX - fleeFrom.posX;
        double d2 = posZ - fleeFrom.posZ;
        float f2 = (float)((Math.atan2(d2, d1) * 180D) / 3.1415927410125732D) - 90F;
        float f3 = f2 - rotationYaw;
        
        rotationYaw = updateRotation2(rotationYaw, f3, 360F);*/
		
		double d = fleeFrom.posX - ent.posX;
        double d1;
        for (d1 = fleeFrom.posZ - ent.posZ; d * d + d1 * d1 < 0.0001D; d1 = (Math.random() - Math.random()) * 0.01D)
        {
            d = (Math.random() - Math.random()) * 0.01D;
        }
        float f = MathHelper.sqrt_double(d * d + d1 * d1);

        //knockBack(entity, i, d, d1);
        
        float yaw = (float)((Math.atan2(d1, d) * 180D) / 3.1415927410125732D)/* - ent.rotationYaw*/;
		
		float look = ent.worldObj.rand.nextInt(15)-7;
        //int height = 10;
        double dist = ent.worldObj.rand.nextInt(2)+4;
        
        //dist adjuster
        if (fleeFrom instanceof EntityCreeper) {
        	dist = ent.worldObj.rand.nextInt(8)+8;
        } else if (fleeFrom instanceof EntitySkeleton) {
        	dist = ent.worldObj.rand.nextInt(8)+16;
        }
        
        if (lifeAtRisk) {
        	dist = 20;
        }
        
        int gatherX = (int)(ent.posX + ((double)(-Math.sin((yaw+look) / 180.0F * 3.1415927F) * Math.cos(ent.rotationPitch / 180.0F * 3.1415927F)) * dist));
        int gatherY = (int)(ent.posY-0.5 + (double)(-MathHelper.sin(ent.rotationPitch / 180.0F * 3.1415927F) * dist) - 0D); //center.posY - 0D;
        int gatherZ = (int)(ent.posZ + ((double)(Math.cos((yaw+look) / 180.0F * 3.1415927F) * Math.cos(ent.rotationPitch / 180.0F * 3.1415927F)) * dist));
        
        gatherX = (int)(ent.posX - (d / f * dist));
        gatherZ = (int)(ent.posZ - (d1 / f * dist));
        
        int id = ent.worldObj.getBlockId(gatherX, gatherY, gatherZ);
        int id2 = ent.worldObj.getBlockId(gatherX, gatherY+1, gatherZ);
        
        int offset = 0;
        
        int finalY = gatherY;
        
        while (offset < 10) {
        	if (id != 0 && id2 == 0) {
        		finalY += offset;
        		break;
        	}
        	
        	//id = ent.worldObj.getBlockId(gatherX, gatherY+offset++, gatherZ);
        	id = ent.worldObj.getBlockId(gatherX, gatherY-offset, gatherZ);
        	id2 = ent.worldObj.getBlockId(gatherX, gatherY-offset+1, gatherZ);
        	
        	if (id != 0 && id2 == 0) {
        		finalY -= offset;
        		break;
        	}
        	
        	//id = ent.worldObj.getBlockId(gatherX, gatherY+offset++, gatherZ);
        	id = ent.worldObj.getBlockId(gatherX, gatherY+offset, gatherZ);
        	id2 = ent.worldObj.getBlockId(gatherX, gatherY+offset+1, gatherZ);
        	
        	offset++;
        }
        
        if (offset < 10) {
        	//System.out.println("flee");
        	//if (c_AIP.i.pathToEntity == null) {
        		c_AIP.i.walkTo(ent, gatherX, finalY, gatherZ, maxPFRange , 600, -1);
        	//}
        	//this.walkTo(this, homeX, homeY, homeZ, maxPFRange, 600);
        } else {
        	//System.out.println("flee failed");
        	//c_AIP.i.walkTo(ent, c_AIP.i.homeX, c_AIP.i.homeY, c_AIP.i.homeZ, maxPFRange, 600, -1);
        }
	}
	
	
	
}
