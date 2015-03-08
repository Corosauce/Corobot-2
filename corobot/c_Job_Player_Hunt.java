package corobot;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.DamageSource;

import java.util.List;

import CoroAI.entity.EnumJobState;
import CoroAI.entity.JobManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class c_Job_Player_Hunt extends c_Job_Player {
	
	public long huntRange = 20;
	public long alarmRange = 6;
	public boolean dontStray = false;
	public boolean xRay = false;
	
	public c_Job_Player_Hunt(JobManager jm) {
		super(jm);
	}
	
	@Override
	public void tick() {
		jobHunter();
	}
	
	@Override
	public boolean shouldExecute() {
		return true;
	}
	
	@Override
	public boolean shouldContinue() {
		return true;//ent.entityToAttack == null;
	}

	@Override
	public void onLowHealth() {
		
		EntityLiving ent = c_AIP.i.player;
		
		if (lastFleeEnt != null) {
			if (hitAndRunDelay == 0 && c_AIP.i.player.getDistanceToEntity(lastFleeEnt) > 6F) {
				hitAndRunDelay = c_AIP.i.cooldown_Ranged+1;
				//c_AIP.i.entityToAttack = ent.lastFleeEnt;
				//if (c_AIP.i.entityToAttack != null) {
				c_AIP.i.attackEntity(lastFleeEnt, ent.getDistanceToEntity(c_AIP.i.player));
				System.out.println("try hit and run");
					//c_AIP.i.rightClickItem();
					//ent.faceEntity(ent.entityToAttack, 180F, 180F);
				//}
				
			} else {
				//ent.entityToAttack = null;
			}
		}
	}
	
	@Override
	public void hitHook(DamageSource ds, int damage) {
		if (c_AIP.i.isEnemy(ds.getEntity())) {
			c_AIP.i.entityToAttack = ds.getEntity();
		}
		
		/*if (ent.getHealth() < ent.getMaxHealth() / 2 && ds.getEntity() == c_CoroAIUtil.getFirstPlayer()) {
			ent.dipl_hostilePlayer = true;
			ent.getGroupInfo(EnumInfo.DIPL_WARN);
		}*/
		 
		
		//temp fun code
		/*if (ds.getEntity() instanceof ZCSdkEntitySentry) {
			ent.entityToAttack = ds.getEntity();
		}*/
	}
	
	@Override
	public void setJobItems() {
		
		//c_CoroAIUtil.setItems_JobHunt(ent);
		
		
	}
	
	protected void jobHunter() {
	
		//ent field rerouting!
		EntityLiving ent = c_AIP.i.player;
		
		if (c_AIP.i.orders == c_EnumAIPOrders.STAY_CLOSE) {
			huntRange = 6;
		} else if (c_AIP.i.orders == c_EnumAIPOrders.STAY_AROUND) {
			huntRange = 24;
		} else if (c_AIP.i.orders == c_EnumAIPOrders.WANDER) {
			huntRange = 48;
		}
		
		//this whole function is crap, redo it bitch
		
		//a use for the states
		
		//responding to alert, so you know to cancel it if alert entity / active target is dead
		
		/*if (tryingToFlee && (onGround || isInWater())) {
			tryingToFlee = false;
			fleeFrom(lastFleeEnt);
		}*/
		
		//huntRange = 24;
		//c_AIP.i.maxDistanceFromHome = 48F;
		//huntRange = 48;
		
		c_AIP.i.enemyClose = false;
		
		//if (true) return;
		
		//health = 8;
		/*if (health < getMaxHealth() * 0.75F) {
			avoid();
			if (rand.nextInt(5) == 0) entityToAttack = null;
		} else {*/
		
		setJobState(EnumJobState.IDLE);
		
		//Creeper safety range
		if (avoid(true, true, 5.0F, false)) {
			//more flee!
			
			c_AIP.i.enemyClose = true;
		//Mob avoid melee range
		} else if (avoid(true, false, 3.5F, false)) {
			c_AIP.i.enemyClose = true;
		} else {
			if (c_AIP.i.entityToAttack != null && c_AIP.i.entityToAttack instanceof EntityLiving && c_AIP.i.player.getDistanceToEntity(c_AIP.i.entityToAttack) < 4F) {
				c_AIP.i.pathToEntity = null;
			}
		
			if (ent.getHealth() > ent.getMaxHealth() * 0.75F && (c_AIP.i.entityToAttack == null || ent.worldObj.rand.nextInt(5) == 0)) {
				boolean found = false;
				Entity clEnt = null;
				Entity clPickup = null;
				float closest = 9999F;
				float closestPickup = 9999F;
		    	List list = ent.worldObj.getEntitiesWithinAABBExcludingEntity(ent, ent.boundingBox.expand(huntRange, huntRange/2, huntRange));
		        for(int j = 0; j < list.size(); j++)
		        {
		            Entity entity1 = (Entity)list.get(j);
		            if(c_AIP.i.isEnemy(entity1))
		            {
		            	if ((xRay || ent.canEntityBeSeen(entity1)) && (ent.posY > entity1.posY-3 && ent.posY < entity1.posY+8)) {
		            		if (sanityCheck(entity1)/* && entity1 instanceof EntityPlayer*/) {
		            			float dist = ent.getDistanceToEntity(entity1);
		            			if (dist < closest) {
		            				closest = dist;
		            				clEnt = entity1;
		            			}
			            		
			            		//found = true;
			            		//break;
		            		}
		            		//this.hasAttacked = true;
		            		//getPathOrWalkableBlock(entity1, 16F);
		            	}
		            } else if (c_AIP.i.isWantedItemorEXP(entity1) && ent.canEntityBeSeen(entity1) && !entity1.isInWater()) {
		            	float dist = ent.getDistanceToEntity(entity1);
            			if (dist < closestPickup) {
            				closestPickup = dist;
            				clPickup = entity1;
            			}
		            }
		        }
		        if (closest < alarmRange) {
		        	c_AIP.i.enemyClose = true;
		        }
		        
		        if (!((JobPlayerInventory)c_AIP.i.job.getPrimaryJobClass()).missingNeededItem() || c_AIP.i.enemyClose) {
		        	if (clPickup != null && (clEnt == null || closest > 14F)) {
			        	c_AIP.i.huntTarget(clPickup);
			        } else if (clEnt != null) {
			        	c_AIP.i.huntTarget(clEnt);
			        }
		        }
		        
		        /*if (!found) {
		        	setState(EnumKoaActivity.IDLE);
		        }*/
			} else {
				
				if (c_AIP.i.entityToAttack != null && c_AIP.i.isWantedItemorEXP(c_AIP.i.entityToAttack) && c_AIP.i.entityToAttack.handleWaterMovement()) c_AIP.i.entityToAttack = null;
				
				if (c_AIP.i.entityToAttack != null) {
					if (c_AIP.i.pathToEntity == null && ent.getDistanceToEntity(c_AIP.i.entityToAttack) > 3F) {
						//PFQueue.getPath(ent, c_AIP.i.entityToAttack, c_AIP.i.maxPFRange);
						c_AIP.i.huntTarget(c_AIP.i.entityToAttack);
					}
				}
				
			}
		}
	
		if (c_AIP.i.enemyClose) {
			/*if (c_AIP.i.pathToEntity != null && c_AIP.i.pathToEntity.points != null) {
				int pIndex = c_AIP.i.pathToEntity.pathIndex+1;
				if (pIndex < c_AIP.i.pathToEntity.points.length) {
					if (ent.worldObj.rayTraceBlocks(Vec3D.createVector((double)c_AIP.i.pathToEntity.points[pIndex].xCoord + 0.5D, (double)c_AIP.i.pathToEntity.points[pIndex].yCoord + 1.5D, (double)c_AIP.i.pathToEntity.points[pIndex].zCoord + 0.5D), Vec3D.createVector(ent.posX, ent.posY + (double)ent.getEyeHeight(), ent.posZ)) == null) {
						c_AIP.i.pathToEntity.pathIndex++;
					}
				}
			}*/
		}
			
			//derp
			/*if (ent.entityToAttack == null && ent.rand.nextInt(6000) == 0) {
				ent.walkTo(ent, ent.homeX, ent.homeY, ent.homeZ, ent.maxPFRange, 600);
			}*/
			
		//}
		ent.prevHealth = ent.getHealth();
	}
	
	
	
	public void hunterHitHook(DamageSource ds, int damage) {
		
		/*if (health < getMaxHealth() / 4 * 3) {
			if (ds.getEntity() != null) {
				lastFleeEnt = ds.getEntity();
				tryingToFlee = true;
				//fleeFrom(ds.getEntity());
			}
		}
		prevKoaHealth = health;*/
	}
	
	public boolean sanityCheck(Entity target) {
		//ent field rerouting!
		EntityLiving ent = c_AIP.i.player;
		if (ent.getHealth() < 10) {
			return false;
		}
		
		if (/*target instanceof EntityCreeper || */target instanceof EntityEnderman || target instanceof EntityWolf || target instanceof EntityPigZombie) {
			return false;
		}
		
		if (dontStray) {
			if (target.getDistance(c_AIP.i.homeX, c_AIP.i.homeY, c_AIP.i.homeZ) > c_AIP.i.maxDistanceFromHome) {
				return false;
			}
		}
		return true;
	}
	
	
	
}
